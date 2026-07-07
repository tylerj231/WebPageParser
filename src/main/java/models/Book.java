package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String title;
    private double price;
    private String availability;
    private int availableQuantity;
    private int rating;
    private String detailUrl;
    private String upc;

}
