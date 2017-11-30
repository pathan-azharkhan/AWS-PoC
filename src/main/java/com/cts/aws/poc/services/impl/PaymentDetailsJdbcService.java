/**
 * 
 */
package com.cts.aws.poc.services.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.amazonaws.services.kms.model.UnsupportedOperationException;
import com.cts.aws.poc.constants.PaymentStatus;
import com.cts.aws.poc.dao.PaymentDetails;
import com.cts.aws.poc.models.FailedPayment;
import com.cts.aws.poc.models.PaymentBatch;
import com.cts.aws.poc.services.PaymentDetailsPersistenceService;
import com.cts.aws.poc.utils.DateUtils;
import com.cts.aws.poc.utils.GeographyUtil;

/**
 * @author Azharkhan
 *
 */
@Service
public class PaymentDetailsJdbcService implements PaymentDetailsPersistenceService {
	
	private static final Logger LOGGER = LogManager.getLogger(PaymentDetailsJdbcService.class);
	
	private static final String QUERY = "SELECT status, txn_currency, count(*) FROM payment_details where created_date = ? group by status, txn_currency";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<PaymentDetails> persistNewBatch(PaymentBatch batch) {
		throw new UnsupportedOperationException("This operation is currently not supported by JDBC service");
	}

	@Override
	public void updatePaymentsOnValidationFailure(List<FailedPayment> failedPayments) {
		throw new UnsupportedOperationException("This operation is currently not supported by JDBC service");
	}
	
	@Override
	public void updatePaymentsOnFileDispatch(List<PaymentDetails> payments, PaymentStatus paymentStatus) {
		throw new UnsupportedOperationException("This operation is currently not supported by JDBC service");
	}

	/**
	 * Region = {(Status = Count)}
	 */
	@Override
	public Map<String, Map<String, Integer>> getDashboardData(Date selectedDate) {
		
		String dateString = DateUtils.MYSQL_DATE_FORMAT.format(selectedDate);
		
		LOGGER.debug("Fetching dashboard data for date {}", dateString);
		
		List<DashboardData> listFromDB = jdbcTemplate.query(QUERY, new Object[] { dateString }, new RowMapper<DashboardData>() {

			@Override
			public DashboardData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new DashboardData(rs.getString(2), rs.getString(1), rs.getInt(3));
			}
		});
		
		Map<String, Map<String, Integer>> response = new HashMap<>();
		
		if (CollectionUtils.isNotEmpty(listFromDB)) {
			
			LOGGER.info("Fetched {} records for date: {}", listFromDB.size(), dateString);
		
			Map<String, List<DashboardData>> dataGroupedByRegion = listFromDB.stream().collect(Collectors.groupingBy(DashboardData::getRegion));
			
			dataGroupedByRegion.forEach((region, dataList) -> {
				
				Map<String, Integer> statusMap = new HashMap<>();
				
				dataList.forEach(data -> {
					
					if (statusMap.get(data.getStatus()) == null)
						statusMap.put(data.getStatus(), data.getCount());
					else {
						statusMap.put(data.getStatus(), statusMap.get(data.getStatus()) + data.getCount());
					}
				});
				
				response.put(region, statusMap);
			});
		} else
			LOGGER.info("No results fetched for date {}", dateString);
		
		return response;
	}
}

class DashboardData {
	
	private String status;
	
	private String region;
	
	private int count;

	public DashboardData(String currency, String status, int count) {
		
		super();
		this.status = status;
		this.count = count;
		region = GeographyUtil.getRegionFromCurrency(currency);
	}

	public String getStatus() {
		return status;
	}

	public String getRegion() {
		return region;
	}

	public int getCount() {
		return count;
	}
}