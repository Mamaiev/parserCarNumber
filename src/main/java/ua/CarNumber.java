package ua;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class CarNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String number;
    private Integer price;
    private String serviceCenter;
    private LocalDateTime added;
    private LocalDateTime changed;
    private boolean deleted;

    @Override
    public String toString() {
        return "CarNumber{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", price=" + price +
                ", serviceCenter='" + serviceCenter + '\'' +
                ", added=" + added +
                ", changed=" + changed +
                ", deleted=" + deleted +
                '}';
    }

    public CarNumber() {
    }

//    public CarNumber(String number, Integer price, String serviceCenter) {
//        this.number = number;
//        this.price = price;
//        this.serviceCenter = serviceCenter;
//    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getServiceCenter() {
        return serviceCenter;
    }

    public void setServiceCenter(String serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    public LocalDateTime getAdded() {
        return added;
    }

    public void setAdded(LocalDateTime added) {
        this.added = added;
    }

    public LocalDateTime getChanged() {
        return changed;
    }

    public void setChanged(LocalDateTime changed) {
        this.changed = changed;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarNumber carNumber = (CarNumber) o;
        return number.equals(carNumber.number) &&
                price.equals(carNumber.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, price);
    }
}
