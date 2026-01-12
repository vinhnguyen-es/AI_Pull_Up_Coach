from enum import Enum


class DebugMode(str, Enum):
    """Debug mode configuration options."""
    DEBUG = "debug"
    DEBUG_NO_SAVE = "debug_no_save"
    NON_DEBUG = "non_debug"

