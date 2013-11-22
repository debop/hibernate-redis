package org.hibernate.test.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * org.hibernate.test.domain.PhoneNumber
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:54
 */
@Getter
@Setter
public class PhoneNumber implements Serializable {

	private static final long serialVersionUID = 8568232753916897060L;

	private long personId = 0;
	private String numberType = "home";
	private long phone = 0;

	public boolean equals(Object obj) {
		if ((obj != null) && (obj instanceof PhoneNumber))
			return hashCode() == obj.hashCode();
		return false;
	}

    @Override
    public int hashCode() {
        int result = (int) (personId ^ (personId >>> 32));
        result = 31 * result + (numberType != null ? numberType.hashCode() : 0);
        result = 31 * result + (int) (phone ^ (phone >>> 32));
        return result;
    }

	public String toString() {
		return numberType + ":" + phone;
	}
}
