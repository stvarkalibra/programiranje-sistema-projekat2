package org.example.sharedmodule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEventDTO {
    private Long bookingId;
    private Double amount;


}
