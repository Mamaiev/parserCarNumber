package ua.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity
public class ChasingNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number; // what is better store number as String of foreign key on CarNumberNumber table id?
    private Long chatId;
    private Long userId; // maybe need remove this field

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChasingNumber that = (ChasingNumber) o;
        return number.equals(that.number) &&
                chatId.equals(that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, chatId);
    }

    @Override
    public String toString() {
        return "ChasingNumber{" +
                "number='" + number + '\'' +
                '}';
    }

    public class ChasingUserNumber {
        private Long userId;
        private List<String> numbers;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public List<String> getNumbers() {
            return numbers;
        }

        public void setNumbers(List<String> numbers) {
            this.numbers = numbers;
        }
    }
}
