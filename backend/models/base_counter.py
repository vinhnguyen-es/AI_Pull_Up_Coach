from collections import deque
from typing import Optional, Tuple

import numpy as np

from utils.keypoint_utils import calculate_wrist_positions
from utils.logging_utils import logger


class Counter:
    POSITION_HISTORY_SIZE = 30
    DIRECTION_HISTORY_SIZE = 10
    LOOKBACK_FRAMES = 5
    STABLE_DECAY_RATE = 0.5

    DIRECTION_UP = "up"
    DIRECTION_DOWN = "down"
    DIRECTION_STABLE = "stable"
    DIRECTION_STARTING = "starting"

    STATUS_PULLING_UP = "pulling_up"
    STATUS_LOWERING_DOWN = "lowering_down"
    STATUS_STABLE = "stable"
    STATUS_NEUTRAL = "neutral"
    STATUS_NO_PERSON = "no_person"
    STATUS_INVALID_KEYPOINTS = "invalid_keypoints"
    STATUS_LOW_CONFIDENCE = "low_confidence"
    STATUS_ERROR = "error"

    def __init__(self, config, logger):
        self.config = config
        self.logger = logger

        self.count = 0
        self.status = self.STATUS_NEUTRAL

        self.position_history = deque(maxlen=self.POSITION_HISTORY_SIZE)
        self.direction_history = deque(maxlen=self.DIRECTION_HISTORY_SIZE)

        self.last_rep_time = 0
        self.current_direction = self.DIRECTION_STABLE
        self.consecutive_up_frames = 0
        self.consecutive_down_frames = 0

        self.frame_count = 0


    def arm_movement_type(self):
        recent_positions = list(self.position_history)[-self.LOOKBACK_FRAMES:]
        recent_positions_length = len(recent_positions)

        recent_lefts = list()
        recent_rights = list()
        left_sum = 0
        right_sum = 0

        for current_pos in recent_positions:
            recent_lefts.append(current_pos["left"])
            recent_rights.append(current_pos["right"])

        for left in recent_lefts:
            left_sum += abs(left)

        for right in recent_rights:
            right_sum += abs(right)

        if (left_sum and right_sum) < self.config.min_arm_movement_threshold: return None
        elif (left_sum < right_sum): return "right"
        elif (left_sum > right_sum): return "left"
        else: return "both"

    def _calculate_movement_from_history(self, moving_arm: str = None, exercise: str = "Pull Ups") -> Tuple[Optional[float], ...]:
            """
            Calculate vertical movement by comparing recent positions.

            Compares the most recent position with the position from LOOKBACK_FRAMES ago.
            This smooths out frame-to-frame noise while still being responsive.

            Returns:
                float: Movement amount (positive = moving up, negative = moving down)
                None: If not enough history yet
            """
            match exercise:
                case "Pull Ups":
                    if len(self.position_history) < self.LOOKBACK_FRAMES:
                        return None

                    recent_positions = list(self.position_history)[-self.LOOKBACK_FRAMES:]

                    movement = recent_positions[-1] - recent_positions[0]
                    return movement
                case "Bicep Curls":
                    if len(self.position_history) < self.LOOKBACK_FRAMES:
                        return None

                    recent_positions = list(self.position_history)[-self.LOOKBACK_FRAMES:]

                    # Determine which arm to track
                    if moving_arm is None:
                        moving_arm = self.arm_movement_type()

                    if moving_arm is None:
                        return None

                        # Extract the relevant arm's position from each dict
                    if moving_arm == "both":
                        # Average both arms
                        first_pos = (recent_positions[0]["left"] + recent_positions[0]["right"]) / 2
                        last_pos = (recent_positions[-1]["left"] + recent_positions[-1]["right"]) / 2
                    else:
                        # Use the specified arm
                        first_pos = recent_positions[0][moving_arm]
                        last_pos = recent_positions[-1][moving_arm]

                    movement = last_pos - first_pos
                    return movement
                case "Jumping Jacks":
                    if len(self.position_history) < self.LOOKBACK_FRAMES:
                        print("returning None")
                        return None

                    recent_positions = list(self.position_history)[-self.LOOKBACK_FRAMES:]

                    def ankle_x_movement(x, y):
                        return x[0] - y[0]

                    def wrist_y_movement(x, y):
                        return x[1] - y[1]

                    def left_ankle_positions_diff(left_pos, prev_left_pos):
                        return ankle_x_movement(left_pos, prev_left_pos)

                    def right_ankle_positions_diff(right_pos, prev_right_pos):
                        return ankle_x_movement(right_pos, prev_right_pos)

                    def left_wrist_positions_diff(left_pos, prev_left_pos):
                        return wrist_y_movement(left_pos, prev_left_pos)

                    def right_wrist_positions_diff(right_pos, prev_right_pos):
                        return wrist_y_movement(right_pos, prev_right_pos)

                    l_movement = left_ankle_positions_diff(recent_positions[-1][0], recent_positions[0][0])
                    r_movement = right_ankle_positions_diff(recent_positions[-1][1], recent_positions[0][1])

                    arm_movement = left_wrist_positions_diff(recent_positions[-1][2], recent_positions[0][2])

                    return l_movement, r_movement, arm_movement


                case "Squats": #TODO#
                    if len(self.position_history) < self.LOOKBACK_FRAMES:
                        return None

                    recent_positions = list(self.position_history)[-self.LOOKBACK_FRAMES:]

                    logger.warning(recent_positions)#either moving up or down#
                    movement = recent_positions[-1] - recent_positions[0]
                    return movement


    def _classify_movement_direction(self, movement: Tuple[Optional[float], ...], exercise = "Pull Ups") -> str:
        """
        Classify movement into up/down/stable based on threshold.

        Uses hysteresis (threshold) to prevent jitter from small movements.
        Remember: wrist_y - shoulder_y gives us negative values when hanging.

        Args:
            movement: The movement amount (positive = up, negative = down)

        Returns:
            str: One of DIRECTION_UP, DIRECTION_DOWN, or DIRECTION_STABLE

        Example:
            If movement = +15 pixels and threshold = 8:
                -> DIRECTION_UP (wrists moving closer to shoulders)
            If movement = -15 pixels:
                -> DIRECTION_DOWN (wrists moving away from shoulders)
            If movement = +3 pixels:
                -> DIRECTION_STABLE (below threshold, ignore)
        """
        LEFT = 0
        RIGHT = 1
        ARM = 2

        print("-"*20)
        print("LEFT:", movement[LEFT], self.config.left_movement_threshold)
        print("RIGHT:", movement[RIGHT], self.config.right_movement_threshold)
        print("ARM:", movement[ARM], self.config.arm_movement_threshold)
        if (movement[LEFT] < -self.config.left_movement_threshold
                and movement[RIGHT] > self.config.right_movement_threshold
                and movement[ARM] > self.config.arm_movement_threshold):
            return self.DIRECTION_UP
        elif (movement[LEFT] > self.config.left_movement_threshold
                and movement[RIGHT] < -self.config.right_movement_threshold
                and movement[ARM] < -self.config.arm_movement_threshold):
            return self.DIRECTION_DOWN
        else:
            return self.DIRECTION_STABLE

    def _update_consecutive_frame_counters(self, detected_direction: str) -> None:
        """
        Update counters that track consecutive frames in each direction.

        This implements confirmation logic: we need multiple consecutive frames
        in a direction before we confirm it as a real movement. This prevents
        noise from causing false direction changes.

        When stable, we gradually decay the counters rather than reset immediately.
        This provides some "memory" and prevents rapid state flipping.

        Args:
            detected_direction: The direction detected in this frame
        """
        if detected_direction == self.DIRECTION_UP:
            self.consecutive_up_frames += 1
            self.consecutive_down_frames = 0
        elif detected_direction == self.DIRECTION_DOWN:
            self.consecutive_down_frames += 1
            self.consecutive_up_frames = 0
        else:  # DIRECTION_STABLE
            # Gradually decay counters instead of immediate reset
            # This prevents jitter when movement briefly becomes stable
            if self.consecutive_up_frames > 0:
                self.consecutive_up_frames = max(0, self.consecutive_up_frames - self.STABLE_DECAY_RATE)
            if self.consecutive_down_frames > 0:
                self.consecutive_down_frames = max(0, self.consecutive_down_frames - self.STABLE_DECAY_RATE)

    def _get_confirmed_direction(self) -> str:
        """
        Get the confirmed direction based on consecutive frame counts.

        A direction is only "confirmed" after min_consecutive_frames in that direction.
        This is the core of our noise reduction: transient movements are ignored.

        Returns:
            str: Confirmed direction (may be same as current_direction if no change)

        State transition rules:
            - Need 3+ consecutive frames to confirm UP or DOWN
            - Only return to STABLE if both counters have decayed to 0
            - Otherwise maintain current confirmed direction
        """
        if self.consecutive_up_frames >= self.config.min_consecutive_frames:
            return self.DIRECTION_UP
        elif self.consecutive_down_frames >= self.config.min_consecutive_frames:
            return self.DIRECTION_DOWN
        elif self.consecutive_up_frames == 0 and self.consecutive_down_frames == 0:
            return self.DIRECTION_STABLE
        else:
            return self.current_direction