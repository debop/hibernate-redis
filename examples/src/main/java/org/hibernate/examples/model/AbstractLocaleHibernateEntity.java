package org.hibernate.examples.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 지역화 정보를 가지는 엔티티의 기본 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 4:09
 */
public abstract class AbstractLocaleHibernateEntity<TId extends Serializable, TLoc extends LocaleValue>
    extends AbstractHibernateEntity<TId> implements LocaleHibernateEntity<TLoc> {

  private TLoc defaultLocaleValue = null;

  abstract public Map<Locale, TLoc> getLocaleMap();

  public TLoc getDefaultLocale() {
    if (defaultLocaleValue == null) {
      defaultLocaleValue = createDefaultLocaleValue();
    }
    return defaultLocaleValue;
  }

  /**
   * Java에서는 실행 시 Generic 수형을 없애버립니다.
   * scala나 c#은 generic으로 인스턴스를 생성할 수 있지만, Java는 불가능합니다.
   * 그래서 이 값을 꼭 구현해 주셔야 합니다.
   *
   * @return TLocalVal 인스턴스
   */
  abstract public TLoc createDefaultLocaleValue();

  @Override
  public TLoc getLocaleValue(final Locale locale) {
    return getLocaleValueOrDefault(locale);
  }

  @Override
  public Set<Locale> getLocales() {
    return getLocaleMap().keySet();
  }

  @Override
  public void addLocaleValue(final Locale locale, final TLoc localeValue) {
    getLocaleMap().put(locale, localeValue);
  }

  @Override
  public void removeLocaleValue(final Locale locale) {
    getLocaleMap().remove(locale);
  }

  @Override
  public TLoc getLocaleValueOrDefault(final Locale locale) {
    if (getLocaleMap() == null || getLocaleMap().size() == 0 || locale == null || locale.getDisplayName() == null)
      return getDefaultLocale();
    else if (getLocaleMap().containsKey(locale))
      return getLocaleMap().get(locale);
    else
      return getDefaultLocale();
  }

  @Override
  public TLoc getCurrentLocaleValue() {
    return getLocaleValueOrDefault(Locale.getDefault());
  }


  private static final long serialVersionUID = 6140003864178774748L;
}
