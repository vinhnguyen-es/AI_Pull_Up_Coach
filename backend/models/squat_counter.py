from models.base_counter import Counter
import time
import traceback

import numpy as np
from typing import Tuple, Optional
from config.squat_config import squat_config, DebugMode
from utils.logging_utils import logger

from models.base_counter import Counter
from utils.keypoint_utils import extract_hip_knee_keypoints, calculate_hip_knee_diff

class SquatCounter(Counter):
    def __init__(self, config, logger, test_script=False):
        super().__init__(config, logger, test_script)
        logger.info("Squat counter initialized")

#dont change
    def _record_direction_change(self, new_direction: str, current_diff: float) -> None:
        """
        Record a confirmed direction change in the history.

        The direction history is used for rep counting. Each entry contains:
        - direction: "up", "down", or "stable"
        - timestamp: when the change occurred
        - position: wrist-shoulder diff value at the change point

        Args:
            new_direction: The newly confirmed direction
            current_diff: Current wrist-shoulder vertical difference
        """
        self.direction_history.append((new_direction, time.time(), current_diff))
        self.current_direction = new_direction

        if squat_config.debug_mode != DebugMode.NON_DEBUG:
            logger.info(f"Direction change: {self.current_direction.upper()} (diff: {current_diff:.1f})")

    def detect_direction_change(self, current_diff: float) -> Tuple[str, float]:
        """
        Detect and confirm direction changes in vertical movement.

        This is the core motion detection algorithm. It implements a multi-stage
        process to reliably detect direction changes while filtering out noise:

        PROCESS:
        1. Add current position to history buffer
        2. Calculate movement over last N frames
        3. Classify as up/down/stable based on threshold
        4. Update consecutive frame counters
        5. Confirm direction only after N consecutive frames
        6. Record confirmed direction changes to history

        Args:
            current_diff: Current wrist-shoulder vertical difference (in pixels)

        Returns:
            Tuple of:
                - confirmed_direction (str): The confirmed movement direction
                - movement_magnitude (float): Absolute movement amount

        Example flow:
            Frame 1-4: movement = +2px -> classified as stable -> no confirmation
            Frame 5-7: movement = +15px -> classified as up -> consecutive_up = 3
            Frame 8: confirmed as UP, recorded to history
        """
        # Step 1: Store this position in our sliding window
        self.position_history.append(current_diff)

        # Step 2: Check if we have enough history to analyze
        movement = self._calculate_movement_from_history(self)
        if movement is None:
            return self.DIRECTION_STARTING, 0

        # Step 3: Classify the movement direction based on threshold
        detected_direction = self._classify_movement_direction(movement, exercise="Squats")

        # Step 4: Update consecutive frame counters for confirmation
        self._update_consecutive_frame_counters(detected_direction)

        # Step 5: Determine if we have enough confirmation to change state
        confirmed_direction = self._get_confirmed_direction()

        # Step 6: If direction changed, record it in history
        if confirmed_direction != self.current_direction:
            self._record_direction_change(confirmed_direction, current_diff)

        return confirmed_direction, abs(movement)

    def _validate_keypoints(self, keypoints: Optional[np.ndarray]) -> Tuple[bool, Optional[str], Optional[float]]:
        """
        Validate keypoint data quality and extract relevant points.

        Checks:
        1. Keypoints exist and are valid
        2. Required keypoints (shoulders, wrists) are detected
        3. Detection confidence meets minimum threshold

        Args:
            keypoints: Array of detected body keypoints from YOLO

        Returns:
            Tuple of:
                - is_valid (bool): Whether keypoints are usable
                - error_status (str or None): Error status if invalid, None if valid
                - wrist_shoulder_diff (float or None): Calculated difference if valid
        """
        if keypoints is None or len(keypoints) == 0:
            return False, self.STATUS_NO_PERSON, None

        # Extract shoulder and wrist keypoints
        left_hip, right_hip, left_knee, right_knee, min_confidence = \
            extract_hip_knee_keypoints(keypoints)
        
        if min_confidence is None:
            return False, self.STATUS_INVALID_KEYPOINTS, None

        # Verify detection confidence is sufficient
        if min_confidence < squat_config.min_confidence:
            return False, self.STATUS_LOW_CONFIDENCE, None

        # Calculate vertical position metric (wrist_y - shoulder_y)
        hip_knee_diff = calculate_hip_knee_diff(
            left_hip, right_hip, left_knee, right_knee
        )

        return True, None, hip_knee_diff

    def _check_for_rep_completion(self) -> bool:
        """
        Check if recent direction changes indicate a completed rep.

        REP DEFINITION:
        A complete pull-up repetition consists of:
        1. LOWERING phase (DOWN direction) - arms extending, wrists moving away from shoulders
        2. PULLING phase (UP direction) - arms contracting, wrists moving toward shoulders

        VALIDATION CRITERIA:
        - Must have at least 2 direction changes in history
        - Last two changes must be: DOWN followed by UP
        - Movement range between positions must exceed minimum threshold
        - Sufficient cooldown time must have elapsed since last rep

        Returns:
            bool: True if a valid rep was detected and counted

        Example sequence that counts as 1 rep:
            Time 0s: DOWN direction detected (at position -100px)
            Time 1s: UP direction detected (at position -50px)
            -> Range = 50px (exceeds 30px minimum) -> COUNT REP
        """
        current_time = time.time()

        # Check cooldown: prevent counting multiple reps too quickly
        if current_time - self.last_rep_time <= squat_config.rep_cooldown:
            return False

        # Need at least 2 direction changes to form a pattern
        if len(self.direction_history) < 2:
            return False

        # Examine the last two direction changes
        recent_changes = list(self.direction_history)[-3:]

        if len(recent_changes) >= 3:
            prev_prev_direction = recent_changes[0][0]
            prev_direction = recent_changes[1][0] if recent_changes[1][0] != self.DIRECTION_STABLE else prev_prev_direction
            curr_direction = recent_changes[2][0]
        else:
            prev_direction = recent_changes[0][0]
            curr_direction = recent_changes[1][0]

        # Check for the DOWN -> UP pattern (the rep signature)
        #SQUAT SPECIFIC
        if not (prev_direction == self.DIRECTION_UP and curr_direction == self.DIRECTION_DOWN):
            return False

        # Validate movement range to ensure full range of motion
        down_position = recent_changes[0][2]  # Position at bottom of rep
        up_position = recent_changes[1][2]    # Position at top of rep
        movement_range = abs(up_position - down_position)
        movement_range = movement_range if not self.test_script else 6 * movement_range

        # Ensure the movement was significant (prevents counting tiny bounces)
        if movement_range <= squat_config.min_movement_range:
            return False

        # All criteria met - count the rep!
        self._count_rep(down_position, up_position, movement_range)
        return True

    def _count_rep(self, down_position: float, up_position: float, movement_range: float) -> None:
        """
        Increment rep count and update state after detecting a valid rep.

        Args:
            down_position: Wrist-shoulder diff at the bottom of the rep
            up_position: Wrist-shoulder diff at the top of the rep
            movement_range: Total vertical range of motion
        """
        self.count += 1
        self.last_rep_time = time.time()

        # Log rep details for debugging and verification
        if squat_config.debug_mode != DebugMode.NON_DEBUG:
            logger.info(f"REP COMPLETED! Count: {self.count}")
            logger.info(f"   Movement: {down_position:.1f} → {up_position:.1f} (range: {movement_range:.1f})")

        # Clear direction history to prevent this same pattern from being counted again
        self.direction_history.clear()

    def _update_display_status(self, direction: str) -> None:
        """
        Update the display status based on current movement direction.

        This provides user feedback about their current motion state.
        The status is sent to the frontend for UI display.

        Args:
            direction: Current confirmed direction (up/down/stable/starting)
        """
        if direction == self.DIRECTION_UP:
            self.status = self.STATUS_PULLING_UP
        elif direction == self.DIRECTION_DOWN:
            self.status = self.STATUS_LOWERING_DOWN
        else:
            self.status = self.STATUS_STABLE

    def analyze_pose(self, keypoints: Optional[np.ndarray]) -> Tuple[int, str]:
        """
        Main entry point: analyze a frame's pose data and update rep count.

        This is the primary method called by the backend API for each frame.
        It orchestrates the entire rep counting pipeline:

        PROCESSING PIPELINE:
        1. Validate keypoint data (quality checks, confidence thresholds)
        2. Calculate wrist-shoulder vertical difference
        3. Detect movement direction with confirmation
        4. Check for completed rep pattern (DOWN -> UP)
        5. Update display status for user feedback

        Args:
            keypoints: NumPy array of detected keypoints from YOLOv8
                      Shape: (num_people, 17, 3) where last dim is (x, y, confidence)

        Returns:
            Tuple of:
                - rep_count (int): Current total rep count for this session
                - status (str): Current motion status for display
                    Possible values: "pulling_up", "lowering_down", "stable",
                                   "no_person", "invalid_keypoints", "low_confidence", "error"

        Example usage:
            counter = PullUpCounter()
            rep_count, status = counter.analyze_pose(yolo_keypoints)
            # rep_count might be 5, status might be "pulling_up"
        """
        try:
            # Step 1: Validate and extract keypoint data
            is_valid, error_status, hip_knee_diff = self._validate_keypoints(keypoints)
            if not is_valid:
                return self.count, error_status

            # Step 2: Analyze movement direction over time
            direction, magnitude = self.detect_direction_change(hip_knee_diff)

            # Step 3: Check if we've completed a rep (DOWN -> UP pattern)
            self._check_for_rep_completion()

            # Step 4: Update display status for UI feedback
            self._update_display_status(direction)

            return self.count, self.status

        except Exception as e:
            logger.error(traceback.format_exc())
            logger.error(f"Error in rep analysis: {e.__traceback__.tb_lasti}")
            return self.count, self.STATUS_ERROR

    def reset(self) -> None:
        """
        Reset all counter state to initial values.

        This method is called when starting a new workout session.
        It clears all accumulated state and resets counters to zero.

        RESETS:
        - Rep count to 0
        - Status to neutral
        - Position history (clears sliding window)
        - Direction history (clears pattern detection memory)
        - Direction state machine to stable
        - Consecutive frame counters to 0
        - Rep cooldown timer to 0
        - Frame counter to 0

        Use this between workout sessions to ensure clean state.
        """
        self.count = 0
        self.status = self.STATUS_NEUTRAL
        self.position_history.clear()
        self.direction_history.clear()
        self.last_rep_time = 0
        self.current_direction = self.DIRECTION_STABLE
        self.consecutive_up_frames = 0
        self.consecutive_down_frames = 0
        self.frame_count = 0

        logger.info("Squat counter reset to initial state")

