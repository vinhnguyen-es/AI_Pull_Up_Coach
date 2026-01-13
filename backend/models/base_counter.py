import time
from collections import deque
from typing import Optional, Tuple
import numpy as np

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



    def _calculate_movement_from_history(self) -> Optional[float]:
            """
            Calculate vertical movement by comparing recent positions.

            Compares the most recent position with the position from LOOKBACK_FRAMES ago.
            This smooths out frame-to-frame noise while still being responsive.

            Returns:
                float: Movement amount (positive = moving up, negative = moving down)
                None: If not enough history yet
            """
            if len(self.position_history) < self.LOOKBACK_FRAMES:
                return None

            recent_positions = list(self.position_history)[-self.LOOKBACK_FRAMES:]
            movement = recent_positions[-1] - recent_positions[0]
            return movement

    def _classify_movement_direction(self, movement: float) -> str:
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
        if movement > config.movement_threshold:
            return self.DIRECTION_UP
        elif movement < -config.movement_threshold:
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
        if self.consecutive_up_frames >= config.min_consecutive_frames:
            return self.DIRECTION_UP
        elif self.consecutive_down_frames >= config.min_consecutive_frames:
            return self.DIRECTION_DOWN
        elif self.consecutive_up_frames == 0 and self.consecutive_down_frames == 0:
            return self.DIRECTION_STABLE
        else:
            return self.current_direction

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

                # Log direction changes in debug mode for troubleshooting
                if config.debug_mode != DebugMode.NON_DEBUG:
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
             movement = self._calculate_movement_from_history()
             if movement is None:
                 return self.DIRECTION_STARTING, 0

             # Step 3: Classify the movement direction based on threshold
             detected_direction = self._classify_movement_direction(movement)

             # Step 4: Update consecutive frame counters for confirmation
             self._update_consecutive_frame_counters(detected_direction)

             # Step 5: Determine if we have enough confirmation to change state
             confirmed_direction = self._get_confirmed_direction()

             # Step 6: If direction changed, record it in history
             if confirmed_direction != self.current_direction:
                 self._record_direction_change(confirmed_direction, current_diff)

             return confirmed_direction, abs(movement)

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
            if config.debug_mode != DebugMode.NON_DEBUG:
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
                is_valid, error_status, wrist_shoulder_diff = self._validate_keypoints(keypoints)
                if not is_valid:
                    return self.count, error_status

                # Step 2: Analyze movement direction over time
                direction, magnitude = self.detect_direction_change(wrist_shoulder_diff)

                # Step 3: Check if we've completed a rep (DOWN -> UP pattern)
                self._check_for_rep_completion()

                # Step 4: Update display status for UI feedback
                self._update_display_status(direction)

                return self.count, self.status

            except Exception as e:
                logger.error(f"Error in rep analysis: {e}")
                return self.count, self.STATUS_ERROR

        def _update_display_status(self, direction: str) -> None:
            if direction == self.DIRECTION_UP:
                self.status = self.STATUS_PULLING_UP
            elif direction == self.DIRECTION_DOWN:
                self.status = self.STATUS_LOWERING_DOWN
            else:
                self.status = self.STATUS_STABLE

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

            logger.info("Pull-up counter reset to initial state")