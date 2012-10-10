package org.ucmtwine.parameter.types;

import intradoc.common.GrammarElement;
import intradoc.provider.Provider;
import intradoc.provider.Providers;
import intradoc.server.Service;

import org.ucmtwine.parameter.Parameter;

public class ProviderParameter extends Parameter {

  public ProviderParameter() {
    super("", Provider.class);
  }

  public ProviderParameter(String name) {
    super(name, Provider.class);
  }

  public ProviderParameter(String name, Class<?> type) {
    super(name, type);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getBinderValue(Service service) {
    String providerName = getStringValue(service.getBinder());
    Provider provider = Providers.getProvider(providerName);

    if (this.required && provider == null) {
      throw new IllegalArgumentException("Provider '" + providerName + "' could not be found.");
    }

    // providers that are in error will throw an exception if they are required
    if (provider != null && provider.isInError() && this.required) {
      throw new IllegalArgumentException("The provider '" + providerName + "' is not configured correctly!");
    }

    return provider;
  }

  /**
   * Returns a result set found by the passed in string value.
   */
  @Override
  public Object getArgumentValue(Object object, Service service) throws ClassCastException {

    if (object instanceof String) {
      Provider provider = Providers.getProvider((String) object);

      if (provider != null) {
        return provider;
      }
    }
    return null;
    // throw new
    // ClassCastException("Must supply the name of a provider as a string");
  }

  /**
   * {@inheritDoc}
   */
  public int getGrammarElementType() {
    // string val because the provider name is passed as a string
    return GrammarElement.STRING_VAL;
  }
}
