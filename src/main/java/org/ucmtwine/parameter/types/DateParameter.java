package org.ucmtwine.parameter.types;

import intradoc.common.GrammarElement;
import intradoc.data.DataBinder;
import intradoc.server.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ucmtwine.parameter.Parameter;

public class DateParameter extends Parameter {

  public DateParameter(String name) {
    super(name, Date.class);
  }

  public DateParameter(String name, Class<?> type) {
    super(name, type);
  }

  @Override
  public Object getBinderValue(Service service) {
    String dateString = getStringValue(service.getBinder());

    Date date = null;

    if (dateString != null) {
      date = getFromString(service.getBinder(), dateString);
    }

    return date;
  }

  private Date getFromString(DataBinder binder, String dateString) {
    Date d = null;
    DateFormat dateFormat = new SimpleDateFormat(binder.m_blDateFormat.toSimplePattern());
    try {
      d = (Date) dateFormat.parse(dateString);

    } catch (Exception e) {
      if (dateString != null) {
        throw new IllegalArgumentException("Parameter " + name + " must be parseable as a date");
      }
    }

    return d;
  }

  public Object getArgumentValue(Object object, Service service) throws ClassCastException {
    if (object instanceof String) {
      return getFromString(service.getBinder(), (String) object);
    }

    return super.getArgumentValue(object, service);
  }

  /**
   * {@inheritDoc}
   */
  public int getGrammarElementType() {
    return GrammarElement.DATE_VAL;
  }
}
