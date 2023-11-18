package ua;

import jakarta.persistence.*;

@Entity
public class CarNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String number;
    private Integer price;
    private String serviceCenter;

    @Override
    public String toString() {
        return "CarNumber{" +
                "number='" + number + '\'' +
                ", price=" + price +
                ", serviceCenter='" + serviceCenter + '\'' +
                '}';
    }

    public CarNumber() {
    }

    public CarNumber(String number, Integer price, String serviceCenter) {
        this.number = number;
        this.price = price;
        this.serviceCenter = serviceCenter;
    }

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
}
