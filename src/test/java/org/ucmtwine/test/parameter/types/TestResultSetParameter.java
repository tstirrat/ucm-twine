package org.ucmtwine.test.parameter.types;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import intradoc.common.ServiceException;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.data.DataResultSet;
import intradoc.data.ResultSet;
import intradoc.server.Service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.ucmtwine.parameter.types.ResultSetParameter;

@RunWith(MockitoJUnitRunner.class)
public class TestResultSetParameter {

  private DataBinder testBinder;

  @Mock
  private Service testService;

  @Before
  public void setUp() throws DataException, ServiceException {

    testBinder = new DataBinder();

    DataResultSet rs = new DataResultSet(new String[] { "field1", "field2", "field3" });

    List<String> row = new ArrayList<String>(3);

    row.add("value1");
    row.add("value2");
    row.add("value3");

    rs.addRowWithList(row);

    testBinder.addResultSet("testResultSet", rs);

    when(testService.getBinder()).thenReturn(testBinder);
  }

  @Test
  public void testGetValueFromBinderIsAResultSet() throws IllegalAccessException {
    DataResultSet rs = (DataResultSet) new ResultSetParameter("testResultSet").getBinderValue(testService);

    assertTrue(ResultSet.class.isAssignableFrom(rs.getClass()));
  }

  @Test
  public void testGetValueFromIdocArgumentsFullResultSet() throws IllegalAccessException {
    DataResultSet rs = (DataResultSet) new ResultSetParameter("testResultSet").getArgumentValue("testResultSet",
        testService);

    assertTrue(ResultSet.class.isAssignableFrom(rs.getClass()));
  }

}
