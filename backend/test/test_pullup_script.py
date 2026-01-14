#!/usr/bin/env python3
"""
Test script for pull-up algorithm using pre-recorded videos.

Usage:
    python test_pullup_video.py path/to/video.mp4
"""

import sys
import cv2
import asyncio
import numpy as np
from pathlib import Path

BACKEND_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(BACKEND_ROOT))

from services.pose_service import pose_service
from models.pull_up_counter import PullUpCounter
from config.pull_up_config import pull_up_config
from utils.logging_utils import logger
from utils.keypoint_utils import calculate_wrist_shoulder_diff, extract_shoulder_wrist_keypoints

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

async def main(video_arg: str):
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
    await pose_service.initialize()
    counter = PullUpCounter(pull_up_config, logger)
    
    cap = cv2.VideoCapture(str(video_path))
    if not cap.isOpened():
        raise RuntimeError(f"Cannot open video: {video_path}")
    
    print("Controls: ESC = quit | SPACE = pause")
    print("-" * 50)
    
    paused = False
    
    # -----------------------------------------------------------------
    # Main loop
    # -----------------------------------------------------------------
    while True:
        if not paused:
            ret, frame = cap.read()
            if not ret:
                break
            
            # Pose detection
            keypoints = pose_service.detect_pose(frame)
            if keypoints is not None:
                draw_pose(frame, keypoints)
                reps, status = counter.analyze_pose(keypoints)
                
                # Calculate and print wrist-shoulder metrics
                left_shoulder, right_shoulder, left_wrist, right_wrist, min_confidence = \
                    extract_shoulder_wrist_keypoints(keypoints)
                
                if min_confidence and min_confidence >= pull_up_config.min_confidence:
                    wrist_shoulder_diff = calculate_wrist_shoulder_diff(
                        left_shoulder, right_shoulder, left_wrist, right_wrist
                    )
                    
                    print(f"Reps: {reps} | Status: {status:15s} | Wrist-Shoulder Diff: {wrist_shoulder_diff:6.1f}")
                else:
                    print(f"Reps: {reps} | Status: {status}")
            else:
                reps, status = counter.count, "no_person"
                print(f"Reps: {reps} | Status: {status}")
            
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
            break
        elif key == 32:    # SPACE
            paused = not paused
    
    cap.release()
    cv2.destroyAllWindows()
    print("-" * 50)
    print(f"Final rep count: {counter.count}")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python test_pullup_video.py <video_path>")
        sys.exit(1)
    
    asyncio.run(main(sys.argv[1]))