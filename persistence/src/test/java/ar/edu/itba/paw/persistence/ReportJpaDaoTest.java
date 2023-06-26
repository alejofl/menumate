package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Report;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.constants.ReportConstants;
import ar.edu.itba.paw.persistence.constants.RestaurantConstants;
import ar.edu.itba.paw.persistence.constants.UserConstants;
import ar.edu.itba.paw.util.PaginatedResult;
import org.junit.Assert;
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
        Assert.assertFalse(maybeReport.isPresent());
    }

    @Test
    public void testGetByIdWhenExisting() {
        final Optional<Report> maybeReport = reportDao.getById(ReportConstants.REPORT_IDS[0]);
        Assert.assertTrue(maybeReport.isPresent());
        Assert.assertEquals(ReportConstants.REPORT_IDS[0], maybeReport.get().getReportId().longValue());
    }

    @Test
    @Rollback
    public void testCreateWithNullReporter() {
        final Report report = reportDao.create(RestaurantConstants.RESTAURANT_ID_WITH_NO_REPORTS, null, "My comment");
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_reports", "report_id = " + report.getReportId() + " AND reporter_user_id IS NULL AND comment = 'My comment'"));
    }

    @Test
    @Rollback
    public void testCreateWithReporter() {
        final Report report = reportDao.create(RestaurantConstants.RESTAURANT_ID_WITH_NO_REPORTS, UserConstants.ACTIVE_USER_ID, "Wow they are selling WHAT now");
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "restaurant_reports", "report_id = " + report.getReportId() + " AND reporter_user_id = " + UserConstants.ACTIVE_USER_ID + " AND comment = 'Wow they are selling WHAT now'"));
    }

    @Test
    public void testGetAllAscending() {
        PaginatedResult<Report> page = reportDao.get(null, null, null, null, false, 1, ReportConstants.REPORT_IDS.length);
        List<Report> results = page.getResult();

        // Assert all IDs are present
        Assert.assertEquals(ReportConstants.REPORT_IDS.length, results.size());
        Assert.assertFalse(Arrays.stream(ReportConstants.REPORT_IDS).anyMatch(
                id -> results.stream().noneMatch(r -> r.getReportId() == id)
        ));

        // Assert sorting is correct. Reports are sorted by unhandled first, and secondly by date.
        for (int i = 1; i < ReportConstants.REPORT_IDS.length; i++) {
            Assert.assertTrue((results.get(i - 1).getDateHandled() == null && results.get(i).getDateHandled() != null) || !results.get(i - 1).getDateReported().isAfter(results.get(i).getDateReported()));
        }
    }

    @Test
    public void testGetAllDescending() {
        PaginatedResult<Report> page = reportDao.get(null, null, null, null, true, 1, ReportConstants.REPORT_IDS.length);
        List<Report> results = page.getResult();

        // Assert all IDs are present
        Assert.assertEquals(ReportConstants.REPORT_IDS.length, results.size());
        Assert.assertFalse(Arrays.stream(ReportConstants.REPORT_IDS).anyMatch(
                id -> results.stream().noneMatch(r -> r.getReportId() == id)
        ));

        // Assert sorting is correct
        for (int i = 1; i < ReportConstants.REPORT_IDS.length; i++)
            Assert.assertFalse(results.get(i).getDateReported().isAfter(results.get(i - 1).getDateReported()));
    }

    @Test
    public void testGetPaging() {
        PaginatedResult<Report> page1 = reportDao.get(null, null, null, null, true, 1, 3);
        PaginatedResult<Report> page2 = reportDao.get(null, null, null, null, true, 2, 3);

        List<Report> results1 = page1.getResult();
        List<Report> results2 = page2.getResult();

        Assert.assertEquals(ReportConstants.REPORT_IDS.length, page1.getTotalCount());
        Assert.assertEquals(ReportConstants.REPORT_IDS.length, page2.getTotalCount());
        Assert.assertEquals(3, results1.size());
        Assert.assertEquals(1, results2.size());

        // Assert that the single result in the second page is not contained in the first page
        Assert.assertFalse(results1.stream().anyMatch(r -> r.getReportId().equals(results2.get(0).getReportId())));

        // Join both lists together and assert all IDs are present
        results1.addAll(results2);
        Assert.assertFalse(Arrays.stream(ReportConstants.REPORT_IDS).anyMatch(
                id -> results1.stream().noneMatch(r -> r.getReportId() == id)
        ));

        // Assert sorting is correct
        for (int i = 1; i < ReportConstants.REPORT_IDS.length; i++)
            Assert.assertFalse(results1.get(i).getDateReported().isAfter(results1.get(i - 1).getDateReported()));
    }

    @Test
    public void testGetUnhandled() {
        List<Report> results = reportDao.get(null, null, null, false, false, 1, ReportConstants.REPORT_IDS.length).getResult();

        Assert.assertEquals(ReportConstants.UNHANDLED_REPORT_IDS.length, results.size());
        Assert.assertFalse(Arrays.stream(ReportConstants.UNHANDLED_REPORT_IDS).anyMatch(
                id -> results.stream().noneMatch(r -> r.getReportId() == id)
        ));

        for (int i = 0; i < results.size(); i++) {
            Assert.assertFalse(results.get(i).getIsHandled());
            if (i > 0)
                Assert.assertFalse(results.get(i - 1).getDateReported().isAfter(results.get(i).getDateReported()));
        }
    }

    @Test
    public void testGetByHandler() {
        List<Report> results = reportDao.get(null, null, UserConstants.USER_ID_MODERATOR_ROLE, true, true, 1, ReportConstants.REPORT_IDS.length).getResult();

        Assert.assertEquals(ReportConstants.HANDLED_REPORT_IDS.length, results.size());
        Assert.assertFalse(Arrays.stream(ReportConstants.HANDLED_REPORT_IDS).anyMatch(
                id -> results.stream().noneMatch(r -> r.getReportId() == id)
        ));

        for (int i = 0; i < results.size(); i++) {
            Assert.assertTrue(results.get(i).getIsHandled());
            if (i > 0)
                Assert.assertFalse(results.get(i).getDateHandled().isAfter(results.get(i - 1).getDateHandled()));
        }
    }

    @Test
    public void testGetByRestaurant() {
        List<Report> results = reportDao.get(ReportConstants.RESTAURANT_IDS_WITH_REPORTS[0], null, null, null, true, 1, ReportConstants.REPORT_IDS.length).getResult();

        Assert.assertEquals(ReportConstants.REPORT_IDS_FROM_FIRST_RESTAURANT.length, results.size());
        Assert.assertFalse(Arrays.stream(ReportConstants.REPORT_IDS_FROM_FIRST_RESTAURANT).anyMatch(
                id -> results.stream().noneMatch(r -> r.getReportId() == id)
        ));

        for (int i = 0; i < results.size(); i++) {
            Assert.assertEquals(ReportConstants.RESTAURANT_IDS_WITH_REPORTS[0], results.get(i).getRestaurantId());
            if (i > 0)
                Assert.assertFalse(results.get(i).getDateReported().isAfter(results.get(i - 1).getDateReported()));
        }

    }

    @Test
    public void testGetByRestaurantWithNoReports() {
        List<Report> results = reportDao.get(RestaurantConstants.RESTAURANT_ID_WITH_NO_REPORTS, null, null, null, true, 1, ReportConstants.REPORT_IDS.length).getResult();
        Assert.assertEquals(results.size(), 0);
    }
}
