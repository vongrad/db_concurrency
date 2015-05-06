package db_concurrency;

interface IStatistics {
	public void putStat(int clientid, StatisticResult statisticResult);
}
