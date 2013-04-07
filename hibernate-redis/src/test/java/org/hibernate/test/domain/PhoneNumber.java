package org.hibernate.test.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * org.hibernate.test.domain.PhoneNumber
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:54
 */
@Getter
@Setter
public class PhoneNumber implements Serializable {

    private long personId = 0;
    private String numberType = "home";
    private long phone = 0;

    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof PhoneNumber))
            return hashCode() == obj.hashCode();
        return false;
    }

    public int hashCode() {
        return Objects.hash(numberType, personId, phone);
    }

    public String toString() {
        return numberType + ":" + phone;
    }
}
