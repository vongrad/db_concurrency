package db_concurrency;

/**
 *
 * @author Preuss
 */
public enum StatisticResult {
	COULD_NOT_RESERVE, RESERVED_DECIDED_NOT_TO_BOOK, BOOKING_OTHER_ERROR, BOOKING_NOT_RESERVED, BOOKING_RESERVED_OTHER, BOOKING_RESERVATION_TIMEOUT, BOOKING_OCCUPIED, BOOKED_SUCCESS, BOOKED_ERROR
}
