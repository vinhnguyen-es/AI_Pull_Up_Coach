import sys
import cv2
import asyncio
import numpy as np
from pathlib import Path

from config.pull_up_config import pull_up_config
from utils.logging_utils import logger

BACKEND_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(BACKEND_ROOT))

from services.pose_service import pose_service

from models.pull_up_counter import PullUpCounter
from models.bicep_curl_counter import BicepCurlCounter
from models.jumping_jack_counter import JumpingJackCounter
from models.push_up_counter import PushUpCounter
from models.sit_up_counter import SitUpCounter
from models.squat_counter import SquatCounter
from models.base_counter import Counter

from config.pull_up_config import pull_up_config, DebugMode
from config.bicep_curl_config import bicep_curl_config
from config.sit_up_config import sit_up_config
from config.squat_config import squat_config
from config.push_up_config import push_up_config
from config.jumping_jack_config import jumping_jack_config


# ---------------------------------------------------------------------
# COCO skeleton connections
# ---------------------------------------------------------------------
SKELETON_CONNECTIONS = [
    (5, 6),
    (5, 7), (7, 9),
    (6, 8), (8, 10),
    (5, 11), (6, 12),
    (11, 12),
    (11, 13), (13, 15),
    (12, 14), (14, 16),
]


def draw_pose(frame: np.ndarray, keypoints: np.ndarray, min_conf: float = 0.3) -> None:
    """Draw pose skeleton and keypoints on frame (in-place)."""

    # Draw skeleton
    for a, b in SKELETON_CONNECTIONS:
        if keypoints[a, 2] > min_conf and keypoints[b, 2] > min_conf:
            pa = tuple(keypoints[a, :2].astype(int))
            pb = tuple(keypoints[b, :2].astype(int))
            cv2.line(frame, pa, pb, (0, 255, 0), 2)

    # Draw keypoints
    for idx, (x, y, conf) in enumerate(keypoints):
        if conf < min_conf:
            continue

        color = (0, 255, 255)      # default
        radius = 5

        if idx in (5, 6):          # shoulders
            color = (255, 0, 0)
            radius = 7
        elif idx in (9, 10):       # wrists
            color = (0, 0, 255)
            radius = 7

        cv2.circle(frame, (int(x), int(y)), radius, color, -1)


async def main(video_arg: str, exercise):
    # -----------------------------------------------------------------
    # Resolve video path safely
    # -----------------------------------------------------------------
    video_path = Path(video_arg)
    if not video_path.is_absolute():
        video_path = BACKEND_ROOT.parent / video_path

    print(f"Opening video: {video_path.resolve()}")

    if not video_path.exists():
        raise RuntimeError(f"Video file does not exist: {video_path}")

    # -----------------------------------------------------------------
    # Initialize services
    # -----------------------------------------------------------------
    print("Initializing pose detection service...")
    await pose_service.initialize()
    print("Pose service initialized successfully")

    print(f"Setting up counter for exercise: {exercise}")
    match exercise:
        case "Pull Ups":
            counter = PullUpCounter(pull_up_config, logger)
        case "Bicep Curls":
            counter = BicepCurlCounter(bicep_curl_config, logger)
        case "Jumping Jacks":
            counter = JumpingJackCounter(jumping_jack_config, logger)
        case "Push Ups":
            counter = PushUpCounter(push_up_config, logger)
        case "Sit Ups":
            counter = SitUpCounter(sit_up_config, logger)
        case "Squats":
            counter = SquatCounter(squat_config, logger)
        case x:
            UNKOWN_EXERCISE_ERROR = f"Attempted to Create Session With Unknown Exercise. \
                Got: {x}. Expected one of: (Pull Ups, Bicep Curls, Jumping Jacks, Push Ups, Sit Ups, Squats)."

            logger.error(UNKOWN_EXERCISE_ERROR)
            raise ValueError(UNKOWN_EXERCISE_ERROR)

    print(f"Counter initialized: {counter.__class__.__name__}")

    cap = cv2.VideoCapture(str(video_path))
    if not cap.isOpened():
        raise RuntimeError(f"Cannot open video: {video_path}")

    fps = cap.get(cv2.CAP_PROP_FPS)
    frame_count = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    print(f"Video loaded: {frame_count} frames @ {fps:.2f} FPS")
    print("Controls: ESC = quit | SPACE = pause")
    print("-" * 50)

    paused = False
    frame_num = 0

    TARGET_FPS = 5.0
    SAMPLE_INTERVAL_MS = 1000.0 / TARGET_FPS  # 200 ms

    last_sample_time_ms = -1.0

    # -----------------------------------------------------------------
    # Main loop
    # -----------------------------------------------------------------
    while True:
        if not paused:
            ret, frame = cap.read()
            if not ret:
                print("End of video reached")
                break

            frame_num += 1

            # Pose detection
            keypoints = pose_service.detect_pose(frame)
            current_time_ms = cap.get(cv2.CAP_PROP_POS_MSEC)

            # Only analyze if enough time has passed (for 5 fps)
            if last_sample_time_ms < 0 or (
                    current_time_ms - last_sample_time_ms >= SAMPLE_INTERVAL_MS
            ):
                last_sample_time_ms = current_time_ms

                keypoints = pose_service.detect_pose(frame)

                if keypoints is not None:
                    draw_pose(frame, keypoints)
                    reps, status = counter.analyze_pose(keypoints)
                else:
                    reps, status = counter.count, "no_person"

            if keypoints is not None:
                draw_pose(frame, keypoints)
                reps, status = counter.analyze_pose(keypoints)
                if frame_num % 30 == 0:
                    print(f"Frame {frame_num}: Pose detected | Reps: {reps} | Status: {status}")
            else:
                reps, status = counter.count, "no_person"
                if frame_num % 30 == 0:
                    print(f"Frame {frame_num}: No person detected")

            # Overlay info
            cv2.putText(
                frame, f"REPS: {reps}",
                (20, 40),
                cv2.FONT_HERSHEY_SIMPLEX, 1.2,
                (255, 255, 255), 3
            )

            cv2.putText(
                frame, f"STATUS: {status}",
                (20, 80),
                cv2.FONT_HERSHEY_SIMPLEX, 0.8,
                (200, 200, 200), 2
            )

            display_frame = cv2.resize(frame, (960, 540))
            cv2.imshow("Pull-Up Pose + Algorithm Debug", display_frame)

        key = cv2.waitKey(1) & 0xFF
        if key == 27:      # ESC
            print("ESC pressed - exiting")
            break
        elif key == 32:    # SPACE
            paused = not paused
            print(f"Playback {'paused' if paused else 'resumed'}")

    print(f"Final rep count: {counter.count}")
    cap.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    if len(sys.argv) < 2:
        sys.exit(1)

    asyncio.run(main(sys.argv[1], sys.argv[2]))