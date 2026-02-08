#
# Copyright (c) 2008-2023, MOVES Institute, Naval Postgraduate School (NPS).
# All rights reserved.
# This work is provided under a BSD open-source license, see project
# license.html and license.txt
#

"""DIS timestamp configuration and conversion utilities.

DIS time units are defined by IEEE DIS Protocol specification as
2^31 - 1 time units per hour. The timestamp field in the PDU header is
four bytes (unsigned int). Each time unit represents approximately
1.67638 microseconds.

Absolute timestamps have their LSB set to 1 (host synchronized to UTC via NTP).
Relative timestamps have their LSB set to 0 (host not synchronized).
"""

from __future__ import annotations

import time
from datetime import datetime, timezone
from enum import IntEnum


class TimestampStyle(IntEnum):
    """Supported timestamp styles."""
    IEEE_ABSOLUTE = 0
    IEEE_RELATIVE = 1
    UNIX = 2


# 2^31 - 1 DIS time units per hour
DIS_TIME_UNITS_PER_HOUR = 2147483647

ABSOLUTE_TIMESTAMP_MASK = 0x00000001
RELATIVE_TIMESTAMP_MASK = 0xFFFFFFFE


class DisTime:
    """DIS timestamp utility for generating and converting DIS timestamps."""

    _timestamp_style = TimestampStyle.IEEE_ABSOLUTE

    @staticmethod
    def get_timestamp_style():
        """Get the current timestamp style."""
        return DisTime._timestamp_style

    @staticmethod
    def set_timestamp_style(style):
        """Set which timestamp style to use.

        Args:
            style: A TimestampStyle value.
        """
        DisTime._timestamp_style = style

    @staticmethod
    def get_current_dis_timestamp():
        """Get the current DIS timestamp based on the configured style.

        Returns:
            Integer timestamp value.
        """
        style = DisTime._timestamp_style
        if style == TimestampStyle.IEEE_ABSOLUTE:
            return DisTime._get_absolute_timestamp()
        elif style == TimestampStyle.IEEE_RELATIVE:
            return DisTime._get_relative_timestamp()
        elif style == TimestampStyle.UNIX:
            return DisTime._get_unix_timestamp()
        return DisTime._get_absolute_timestamp()

    @staticmethod
    def _get_dis_time_units_since_top_of_hour():
        """Compute DIS time units since the top of the current hour.

        Returns:
            Integer DIS time units.
        """
        now = time.time()
        seconds_since_hour = now % 3600.0
        fraction_of_hour = seconds_since_hour / 3600.0
        return int(fraction_of_hour * DIS_TIME_UNITS_PER_HOUR)

    @staticmethod
    def _get_absolute_timestamp():
        """IEEE absolute timestamp (LSB = 1, host synchronized to UTC)."""
        value = DisTime._get_dis_time_units_since_top_of_hour()
        value = (value << 1) | ABSOLUTE_TIMESTAMP_MASK
        return value & 0xFFFFFFFF

    @staticmethod
    def _get_relative_timestamp():
        """IEEE relative timestamp (LSB = 0, host not synchronized)."""
        value = DisTime._get_dis_time_units_since_top_of_hour()
        value = (value << 1) & RELATIVE_TIMESTAMP_MASK
        return value & 0xFFFFFFFF

    @staticmethod
    def _get_unix_timestamp():
        """Unix timestamp (seconds since 1 January 1970)."""
        return int(time.time()) & 0xFFFFFFFF

    @staticmethod
    def to_datetime(timestamp):
        """Convert a Unix-style DIS timestamp to a datetime object.

        Args:
            timestamp: Integer timestamp in seconds since epoch.

        Returns:
            A datetime object in UTC.
        """
        return datetime.fromtimestamp(timestamp, tz=timezone.utc)

    @staticmethod
    def dis_units_to_seconds(dis_units):
        """Convert DIS time units to seconds within the hour.

        Args:
            dis_units: Raw DIS time units (31-bit value, before LSB flag).

        Returns:
            Float seconds since top of hour.
        """
        return (dis_units / DIS_TIME_UNITS_PER_HOUR) * 3600.0
