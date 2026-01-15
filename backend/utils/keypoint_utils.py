"""
Keypoint extraction utilities for YOLO pose detection.

Provides helper functions for extracting and validating specific keypoints
from YOLO pose estimation results.
"""

from typing import Optional, Tuple
from utils.logging_utils import logger
import numpy as np

# YOLO pose keypoint indices (COCO format)
# 0: nose, 1: left eye, 2: right eye, 3: left ear, 4: right ear
# 5: left shoulder, 6: right shoulder, 7: left elbow, 8: right elbow
# 9: left wrist, 10: right wrist, 11: left hip, 12: right hip
# 13: left knee, 14: right knee, 15: left ankle, 16: right ankle

KEYPOINT_LEFT_SHOULDER = 5
KEYPOINT_RIGHT_SHOULDER = 6
KEYPOINT_LEFT_WRIST = 9
KEYPOINT_RIGHT_WRIST = 10

KEYPOINT_LEFT_ANKLE = 15
KEYPOINT_RIGHT_ANKLE = 16

KEYPOINT_LEFT_HIP = 11
KEYPOINT_RIGHT_HIP = 12
KEYPOINT_LEFT_KNEE = 13
KEYPOINT_RIGHT_KNEE = 14

ARM_MOVEMENT_BOTH = "BOTH"
ARM_MOVEMENT_LEFT = "LEFT"
ARM_MOVEMENT_RIGHT = "RIGHT"
ARM_MOVEMENT_NEITHER = None

HORIZONTAL_MOVEMENT_INDEX = 0
VERTICAL_MOVEMENT_INDEX = 1

def extract_shoulder_wrist_keypoints(
    keypoints: np.ndarray,
) -> Tuple[Optional[np.ndarray], Optional[np.ndarray], Optional[np.ndarray], Optional[np.ndarray], Optional[float]]:
    """Extract shoulder and wrist keypoints with confidence values.

    Args:
        keypoints: YOLO pose keypoints array of shape (17, 3) where each keypoint
                  has [x, y, confidence]

    Returns:
        Tuple of (left_shoulder, right_shoulder, left_wrist, right_wrist, min_confidence)
        where each keypoint is array [x, y, confidence], or (None, None, None, None, None)
        if keypoints array is invalid or has insufficient shape.

    Example:
        >>> keypoints = np.array([[x, y, conf], ...])  # 17 keypoints
        >>> ls, rs, lw, rw, min_conf = extract_shoulder_wrist_keypoints(keypoints)
        >>> if min_conf is not None and min_conf > 0.3:
        ...     # Use the keypoints
    """
    if keypoints is None or len(keypoints) < 17:
        return None, None, None, None, None

    try:
        left_shoulder = keypoints[KEYPOINT_LEFT_SHOULDER]
        right_shoulder = keypoints[KEYPOINT_RIGHT_SHOULDER]
        left_wrist = keypoints[KEYPOINT_LEFT_WRIST]
        right_wrist = keypoints[KEYPOINT_RIGHT_WRIST]

        # Calculate minimum confidence among these 4 keypoints
        min_confidence = min(
            left_shoulder[2],
            right_shoulder[2],
            left_wrist[2],
            right_wrist[2]
        )

        return left_shoulder, right_shoulder, left_wrist, right_wrist, min_confidence

    except (IndexError, TypeError):
        return None, None, None, None, None

def extract_ankle_keypoints(
        keypoints: np.ndarray,
) -> Tuple[Optional[np.ndarray], Optional[np.ndarray], Optional[float]]:
    """Extract shoulder and wrist keypoints with confidence values.

    Args:
        keypoints: YOLO pose keypoints array of shape (17, 3) where each keypoint
                  has [x, y, confidence]

    Returns:
        Tuple of (left_shoulder, right_shoulder, left_wrist, right_wrist, min_confidence)
        where each keypoint is array [x, y, confidence], or (None, None, None, None, None)
        if keypoints array is invalid or has insufficient shape.

    Example:
        >>> keypoints = np.array([[x, y, conf], ...])  # 17 keypoints
        >>> la, ra, min_conf = extract_ankle_keypoints(keypoints)
        >>> if min_conf is not None and min_conf > 0.3:
        ...     # Use the keypoints
    """
    if keypoints is None or len(keypoints) < 17:
        return None, None, None, None, None

    try:
        left_ankle = keypoints[KEYPOINT_LEFT_ANKLE]
        right_ankle = keypoints[KEYPOINT_RIGHT_ANKLE]

        # Calculate minimum confidence among these 4 keypoints
        min_confidence = min(
            left_ankle[2],
            right_ankle[2],
        )

        return left_ankle, right_ankle, min_confidence

    except (IndexError, TypeError):
        return None, None, None

def extract_wrist_keypoints(
        keypoints: np.ndarray,
) -> Tuple[Optional[np.ndarray], Optional[np.ndarray], Optional[float]]:
    """Extract shoulder and wrist keypoints with confidence values.

    Args:
        keypoints: YOLO pose keypoints array of shape (17, 3) where each keypoint
                  has [x, y, confidence]

    Returns:
        Tuple of (left_shoulder, right_shoulder, left_wrist, right_wrist, min_confidence)
        where each keypoint is array [x, y, confidence], or (None, None, None, None, None)
        if keypoints array is invalid or has insufficient shape.

    Example:
        >>> keypoints = np.array([[x, y, conf], ...])  # 17 keypoints
        >>> la, ra, min_conf = extract_ankle_keypoints(keypoints)
        >>> if min_conf is not None and min_conf > 0.3:
        ...     # Use the keypoints
    """
    if keypoints is None or len(keypoints) < 17:
        return None, None, None, None, None

    try:
        left_wrist = keypoints[KEYPOINT_LEFT_WRIST]
        right_wrist = keypoints[KEYPOINT_RIGHT_WRIST]

        # Calculate minimum confidence among these 4 keypoints
        min_confidence = min(
            left_wrist[2],
            right_wrist[2],
        )

        return left_wrist, right_wrist, min_confidence

    except (IndexError, TypeError):
        return None, None, None

def calculate_wrist_shoulder_diff(
    left_shoulder: np.ndarray,
    right_shoulder: np.ndarray,
    left_wrist: np.ndarray,
    right_wrist: np.ndarray,
    arm: str = "both",
    direction: str = "vertical"
) -> float:
    """Calculate vertical difference between average wrist and shoulder positions.

    This metric is used to determine if the person is at the top (wrists above shoulders)
    or bottom (wrists below shoulders) of a pull-up motion.

    Args:
        left_shoulder: Left shoulder keypoint [x, y, confidence]
        right_shoulder: Right shoulder keypoint [x, y, confidence]
        left_wrist: Left wrist keypoint [x, y, confidence]
        right_wrist: Right wrist keypoint [x, y, confidence]

    Returns:
        Vertical difference (wrist_y - shoulder_y) in pixels.
        Positive values indicate wrists are below shoulders (hanging position).
        Negative values indicate wrists are above shoulders (pulled up position).

    Example:
        >>> diff = calculate_wrist_shoulder_diff(ls, rs, lw, rw)
        >>> if diff > 30:
        ...     print("Person is in hanging position")
        >>> elif diff < -30:
        ...     print("Person is in pulled-up position")
    """
    # Obtain shoulder and wrist positions
    index = HORIZONTAL_MOVEMENT_INDEX if direction == "horizontal" else VERTICAL_MOVEMENT_INDEX
    match arm:
        case "right":
            shoulder = right_shoulder[index]
            wrist = right_wrist[index]
        case "left":
            shoulder = left_shoulder[index]
            wrist = left_wrist[index]
        case "both": # Average movements among arms
            shoulder = (left_shoulder[index] + right_shoulder[index]) / 2
            wrist = (left_wrist[index] + right_wrist[index]) / 2
        case _:
            logger.log(f"Arm was: {arm}. Expected one of (both, left, right).")
            raise ValueError(f"Arm was: {arm}. Expected one of (both, left, right).")
    return wrist - shoulder

def calculate_ankle_positions(
        left_ankle,
        right_ankle
) -> float:
    """Calculate vertical difference between average wrist and shoulder positions.

    This metric is used to determine if the person is at the top (wrists above shoulders)
    or bottom (wrists below shoulders) of a pull-up motion.

    Args:
        left_shoulder: Left shoulder keypoint [x, y, confidence]
        right_shoulder: Right shoulder keypoint [x, y, confidence]
        left_wrist: Left wrist keypoint [x, y, confidence]
        right_wrist: Right wrist keypoint [x, y, confidence]

    Returns:
        Vertical difference (wrist_y - shoulder_y) in pixels.
        Positive values indicate wrists are below shoulders (hanging position).
        Negative values indicate wrists are above shoulders (pulled up position).

    Example:
        >>> diff = calculate_ankle_position(la, ra)
    """
    # Obtain shoulder and wrist positions
    return (left_ankle[0], left_ankle[1]), (right_ankle[0], right_ankle[1])

def calculate_wrist_positions(left_wrist, right_wrist):
    return (left_wrist[0], left_wrist[1]), (right_wrist[0], right_wrist[1])


def extract_hip_knee_keypoints(
        keypoints: np.ndarray,
) -> Tuple[Optional[np.ndarray], Optional[np.ndarray], Optional[np.ndarray], Optional[np.ndarray], Optional[float]]:
    """Extract hip and knee keypoints with confidence values.

    Args:
        keypoints: YOLO pose keypoints array of shape (17, 3) where each keypoint
                  has [x, y, confidence]

    Returns:
        Tuple of (left_hip, right_hip, left_knee, right_knee, min_confidence)
        where each keypoint is array [x, y, confidence], or (None, None, None, None, None)
        if keypoints array is invalid or has insufficient shape.

    Example:
        >>> keypoints = np.array([[x, y, conf], ...])  # 17 keypoints
        >>> lh, rh, lk rk, min_conf = extract_hip_knee_keypoints(keypoints)
        >>> if min_conf is not None and min_conf > 0.3:
        ...     # Use the keypoints
    """
    if keypoints is None or len(keypoints) < 17:
        return None, None, None, None, None

    try:
        left_hip = keypoints[KEYPOINT_LEFT_HIP]
        right_hip = keypoints[KEYPOINT_RIGHT_HIP]
        left_knee = keypoints[KEYPOINT_LEFT_KNEE]
        right_knee = keypoints[KEYPOINT_RIGHT_KNEE]

        # Calculate minimum confidence among these 4 keypoints
        min_confidence = min(
            left_hip[2],
            right_hip[2],
            left_knee[2],
            right_knee[2]
        )

        return left_hip, right_hip, left_knee, right_knee, min_confidence

    except (IndexError, TypeError):
        return None, None, None, None, None


def calculate_hip_knee_diff(
        left_hip: np.ndarray,
        right_hip: np.ndarray,
        left_knee: np.ndarray,
        right_knee: np.ndarray,
        direction: str = "vertical"
) -> float:
    """Calculate vertical difference between average wrist and shoulder positions.

    This metric is used to determine if the person is at the top (wrists above shoulders)
    or bottom (wrists below shoulders) of a pull-up motion.

    Args:
        left_shoulder: Left shoulder keypoint [x, y, confidence]
        right_shoulder: Right shoulder keypoint [x, y, confidence]
        left_wrist: Left wrist keypoint [x, y, confidence]
        right_wrist: Right wrist keypoint [x, y, confidence]

    Returns:
        Vertical difference (wrist_y - shoulder_y) in pixels.
        Positive values indicate wrists are below shoulders (hanging position).
        Negative values indicate wrists are above shoulders (pulled up position).

    Example:
        >>> diff = calculate_wrist_shoulder_diff(ls, rs, lw, rw)
        >>> if diff > 30:
        ...     print("Person is in hanging position")
        >>> elif diff < -30:
        ...     print("Person is in pulled-up position")
    """
    # Obtain shoulder and wrist positions
    index = HORIZONTAL_MOVEMENT_INDEX if direction == "horizontal" else VERTICAL_MOVEMENT_INDEX

    hip_y = (left_hip[1] + right_hip[1]) / 2
    knee_y = (left_knee[1] + left_knee[1]) / 2
    # squat down = hip y increases
    # stand up = hip y decreases

    answer = abs(hip_y - knee_y)

    # logger.log(f"Arm was: {arm}. Expected one of (both, left, right).")
    # raise ValueError(f"Arm was: {arm}. Expected one of (both, left, right).")
    logger.info(f"Hip y avg: {hip_y}. Knee y avg: {knee_y}. Difference: {answer}")

    # return wrist - shoulder
    return answer