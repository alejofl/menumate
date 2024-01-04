package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.ReportNotFoundException;
import ar.edu.itba.paw.model.Report;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.ReportConstants;
import ar.edu.itba.paw.persistence.constants.RestaurantConstants;
import ar.edu.itba.paw.persistence.constants.UserConstants;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ReportJpaDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private ReportJpaDao reportDao;

    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testGetByIdWhenNonExisting() {
        final Optional<Report> maybeReport = reportDao.getById(ReportConstants.NONEXISTING_REPORT_ID);
        assertFalse(maybeReport.isPresent());
    }

    @Test
    public void testGetByIdWhenExisting() {
        final Optional<Report> maybeReport = reportDao.getById(ReportConstants.REPORT_IDS[0]);
        assertTrue(maybeReport.isPresent());
        assertEquals(ReportConstants.REPORT_IDS[0], maybeReport.get().getReportId().longValue());
    }

    @Test
    @Rollback
    public void testDeleteExistingReport() {
        reportDao.delete(ReportConstants.REPORT_IDS[0]);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_reports", "report_id = " + ReportConstants.REPORT_IDS[0]));
    }

    @Test(expected = ReportNotFoundException.class)
    public void testDeleteNonExistingReport() {
        reportDao.delete(ReportConstants.NONEXISTING_REPORT_ID);
    }

    @Test
    @Rollback
    public void testCreateWithNullReporter() {
        final Report report = reportDao.create(RestaurantConstants.RESTAURANT_ID_WITH_NO_REPORTS, null, "My comment");
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_reports", "report_id = " + report.getReportId() + " AND reporter_user_id IS NULL AND comment = 'My comment'"));
    }

    @Test
    @Rollback
    public void testCreateWithReporter() {
        final Report report = reportDao.create(RestaurantConstants.RESTAURANT_ID_WITH_NO_REPORTS, UserConstants.ACTIVE_USER_ID, "Wow they are selling WHAT now");
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_reports", "report_id = " + report.getReportId() + " AND reporter_user_id = " + UserConstants.ACTIVE_USER_ID + " AND comment = 'Wow they are selling WHAT now'"));
    }

    @Test
    public void testGetAllAscending() {
        PaginatedResult<Report> page = reportDao.get(null, null, null, null, false, 1, ReportConstants.REPORT_IDS.length);
        List<Report> results = page.getResult();

        // Assert all IDs are present
        assertEquals(ReportConstants.REPORT_IDS.length, results.size());
        assertFalse(Arrays.stream(ReportConstants.REPORT_IDS).anyMatch(
                id -> results.stream().noneMatch(r -> r.getReportId() == id)
        ));

        // Assert sorting is correct. Reports are sorted by unhandled first, and secondly by date.
        for (int i = 1; i < ReportConstants.REPORT_IDS.length; i++) {
            assertTrue((results.get(i - 1).getDateHandled() == null && results.get(i).getDateHandled() != null) || !results.get(i - 1).getDateReported().isAfter(results.get(i).getDateReported()));
        }
    }

    @Test
    public void testGetAllDescending() {
        PaginatedResult<Report> page = reportDao.get(null, null, null, null, true, 1, ReportConstants.REPORT_IDS.length);
        List<Report> results = page.getResult();

        // Assert all IDs are present
        assertEquals(ReportConstants.REPORT_IDS.length, results.size());
        assertFalse(Arrays.stream(ReportConstants.REPORT_IDS).anyMatch(
                id -> results.stream().noneMatch(r -> r.getReportId() == id)
        ));

        // Assert sorting is correct
        for (int i = 1; i < ReportConstants.REPORT_IDS.length; i++)
            assertFalse(results.get(i).getDateReported().isAfter(results.get(i - 1).getDateReported()));
    }

    @Test
    public void testGetPaging() {
        PaginatedResult<Report> page1 = reportDao.get(null, null, null, null, true, 1, 3);
        PaginatedResult<Report> page2 = reportDao.get(null, null, null, null, true, 2, 3);

        List<Report> results1 = page1.getResult();
        List<Report> results2 = page2.getResult();

        assertEquals(ReportConstants.REPORT_IDS.length, page1.getTotalCount());
        assertEquals(ReportConstants.REPORT_IDS.length, page2.getTotalCount());
        assertEquals(3, results1.size());
        assertEquals(1, results2.size());

        // Assert that the single result in the second page is not contained in the first page
        assertFalse(results1.stream().anyMatch(r -> r.getReportId().equals(results2.get(0).getReportId())));

        // Join both lists together and assert all IDs are present
        results1.addAll(results2);
        assertFalse(Arrays.stream(ReportConstants.REPORT_IDS).anyMatch(
                id -> results1.stream().noneMatch(r -> r.getReportId() == id)
        ));

        // Assert sorting is correct
        for (int i = 1; i < ReportConstants.REPORT_IDS.length; i++)
            assertFalse(results1.get(i).getDateReported().isAfter(results1.get(i - 1).getDateReported()));
    }

    @Test
    public void testGetUnhandled() {
        List<Report> results = reportDao.get(null, null, null, false, false, 1, ReportConstants.REPORT_IDS.length).getResult();

        assertEquals(ReportConstants.UNHANDLED_REPORT_IDS.length, results.size());
        assertFalse(Arrays.stream(ReportConstants.UNHANDLED_REPORT_IDS).anyMatch(
                id -> results.stream().noneMatch(r -> r.getReportId() == id)
        ));

        for (int i = 0; i < results.size(); i++) {
            assertFalse(results.get(i).getIsHandled());
            if (i > 0)
                assertFalse(results.get(i - 1).getDateReported().isAfter(results.get(i).getDateReported()));
        }
    }

    @Test
    public void testGetByHandler() {
        List<Report> results = reportDao.get(null, null, UserConstants.USER_ID_MODERATOR_ROLE, true, true, 1, ReportConstants.REPORT_IDS.length).getResult();

        assertEquals(ReportConstants.HANDLED_REPORT_IDS.length, results.size());
        assertFalse(Arrays.stream(ReportConstants.HANDLED_REPORT_IDS).anyMatch(
                id -> results.stream().noneMatch(r -> r.getReportId() == id)
        ));

        for (int i = 0; i < results.size(); i++) {
            assertTrue(results.get(i).getIsHandled());
            if (i > 0)
                assertFalse(results.get(i).getDateHandled().isAfter(results.get(i - 1).getDateHandled()));
        }
    }

    @Test
    public void testGetByRestaurant() {
        List<Report> results = reportDao.get(ReportConstants.RESTAURANT_IDS_WITH_REPORTS[0], null, null, null, true, 1, ReportConstants.REPORT_IDS.length).getResult();

        assertEquals(ReportConstants.REPORT_IDS_FROM_FIRST_RESTAURANT.length, results.size());
        assertFalse(Arrays.stream(ReportConstants.REPORT_IDS_FROM_FIRST_RESTAURANT).anyMatch(
                id -> results.stream().noneMatch(r -> r.getReportId() == id)
        ));

        for (int i = 0; i < results.size(); i++) {
            assertEquals(ReportConstants.RESTAURANT_IDS_WITH_REPORTS[0], results.get(i).getRestaurantId());
            if (i > 0)
                assertFalse(results.get(i).getDateReported().isAfter(results.get(i - 1).getDateReported()));
        }

    }

    @Test
    public void testGetByRestaurantWithNoReports() {
        List<Report> results = reportDao.get(RestaurantConstants.RESTAURANT_ID_WITH_NO_REPORTS, null, null, null, true, 1, ReportConstants.REPORT_IDS.length).getResult();
        assertEquals(0, results.size());
    }

    @Rollback
    @Test
    public void testGetCount() {
        jdbcTemplate.execute("INSERT INTO restaurant_reports (report_id, restaurant_id, reporter_user_id, handler_user_id, date_reported, date_handled, comment) VALUES (2222, "+ReportConstants.RESTAURANT_IDS_WITH_REPORTS[0]+", null, null, now(), null, 'This restaurant sucks')");
        jdbcTemplate.execute("INSERT INTO restaurant_reports (report_id, restaurant_id, reporter_user_id, handler_user_id, date_reported, date_handled, comment) VALUES (2223, "+ReportConstants.RESTAURANT_IDS_WITH_REPORTS[0]+", null, null, now(), null, 'There is a weird smell on this website')");
        jdbcTemplate.execute("INSERT INTO restaurant_reports (report_id, restaurant_id, reporter_user_id, handler_user_id, date_reported, date_handled, comment) VALUES (2224, "+ReportConstants.RESTAURANT_IDS_WITH_REPORTS[1]+", null, null, now(), null, 'I do not like the color red in their logo')");
        jdbcTemplate.execute("INSERT INTO restaurant_reports (report_id, restaurant_id, reporter_user_id, handler_user_id, date_reported, date_handled, comment) VALUES (2225, "+ReportConstants.RESTAURANT_IDS_WITH_REPORTS[1]+", null, null, now(), null, 'I also do not like the color red in their logo')");
        jdbcTemplate.execute("INSERT INTO restaurant_reports (report_id, restaurant_id, reporter_user_id, handler_user_id, date_reported, date_handled, comment) VALUES (2226, "+RestaurantConstants.RESTAURANT_ID_WITH_NO_REPORTS+", null, "+UserConstants.USER_ID_MODERATOR_ROLE+", now(), now(), 'I wish I could afford this')");

        PaginatedResult<Pair<Restaurant, Integer>> page = reportDao.getCountByRestaurant(1, RestaurantConstants.RESTAURANT_IDS.length);
        List<Pair<Restaurant, Integer>> results = page.getResult();

        assertEquals(RestaurantConstants.RESTAURANT_IDS.length, page.getTotalCount());
        assertEquals(RestaurantConstants.RESTAURANT_IDS.length, results.size());
        assertEquals(ReportConstants.RESTAURANT_IDS_WITH_REPORTS[0], results.get(0).getKey().getRestaurantId().longValue());
        assertEquals(3, results.get(0).getValue().intValue());
        assertEquals(ReportConstants.RESTAURANT_IDS_WITH_REPORTS[1], results.get(1).getKey().getRestaurantId().longValue());
        assertEquals(2, results.get(1).getValue().intValue());
        assertEquals(ReportConstants.RESTAURANT_IDS_WITH_REPORTS[2], results.get(2).getKey().getRestaurantId().longValue());
        assertEquals(1, results.get(2).getValue().intValue());
        assertEquals(RestaurantConstants.RESTAURANT_ID_WITH_NO_REPORTS, results.get(3).getKey().getRestaurantId().longValue());
        assertEquals(0, results.get(3).getValue().intValue());
    }

    @Rollback
    @Test
    public void testGetCountPaging() {
        jdbcTemplate.execute("INSERT INTO restaurant_reports (report_id, restaurant_id, reporter_user_id, handler_user_id, date_reported, date_handled, comment) VALUES (2222, "+ReportConstants.RESTAURANT_IDS_WITH_REPORTS[0]+", null, null, now(), null, 'This restaurant sucks')");
        jdbcTemplate.execute("INSERT INTO restaurant_reports (report_id, restaurant_id, reporter_user_id, handler_user_id, date_reported, date_handled, comment) VALUES (2223, "+ReportConstants.RESTAURANT_IDS_WITH_REPORTS[0]+", null, null, now(), null, 'There is a weird smell on this website')");
        jdbcTemplate.execute("INSERT INTO restaurant_reports (report_id, restaurant_id, reporter_user_id, handler_user_id, date_reported, date_handled, comment) VALUES (2224, "+ReportConstants.RESTAURANT_IDS_WITH_REPORTS[1]+", null, null, now(), null, 'I do not like the color red in their logo')");
        jdbcTemplate.execute("INSERT INTO restaurant_reports (report_id, restaurant_id, reporter_user_id, handler_user_id, date_reported, date_handled, comment) VALUES (2225, "+ReportConstants.RESTAURANT_IDS_WITH_REPORTS[1]+", null, null, now(), null, 'I also do not like the color red in their logo')");
        jdbcTemplate.execute("INSERT INTO restaurant_reports (report_id, restaurant_id, reporter_user_id, handler_user_id, date_reported, date_handled, comment) VALUES (2226, "+RestaurantConstants.RESTAURANT_ID_WITH_NO_REPORTS+", null, "+UserConstants.USER_ID_MODERATOR_ROLE+", now(), now(), 'I wish I could afford this')");

        PaginatedResult<Pair<Restaurant, Integer>> page1 = reportDao.getCountByRestaurant(1, 3);
        PaginatedResult<Pair<Restaurant, Integer>> page2 = reportDao.getCountByRestaurant(2, 3);

        List<Pair<Restaurant, Integer>> results1 = page1.getResult();
        List<Pair<Restaurant, Integer>> results2 = page2.getResult();

        assertEquals(RestaurantConstants.RESTAURANT_IDS.length, page1.getTotalCount());
        assertEquals(3, results1.size());
        assertEquals(RestaurantConstants.RESTAURANT_IDS.length, page2.getTotalCount());
        assertEquals(1, results2.size());

        assertEquals(ReportConstants.RESTAURANT_IDS_WITH_REPORTS[0], results1.get(0).getKey().getRestaurantId().longValue());
        assertEquals(3, results1.get(0).getValue().intValue());
        assertEquals(ReportConstants.RESTAURANT_IDS_WITH_REPORTS[1], results1.get(1).getKey().getRestaurantId().longValue());
        assertEquals(2, results1.get(1).getValue().intValue());
        assertEquals(ReportConstants.RESTAURANT_IDS_WITH_REPORTS[2], results1.get(2).getKey().getRestaurantId().longValue());
        assertEquals(1, results1.get(2).getValue().intValue());
        assertEquals(RestaurantConstants.RESTAURANT_ID_WITH_NO_REPORTS, results2.get(0).getKey().getRestaurantId().longValue());
        assertEquals(0, results2.get(0).getValue().intValue());
    }
}
