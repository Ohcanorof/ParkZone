package model;

//this and PaymentGateway are to simulate payment, no real money is being moved around
public interface PaymentProcessor {
    double calculateFee(Ticket t, double hourlyRate);
    boolean takePayment(Ticket t, String cardToken);
    String generateReceipt(Ticket t);
}

// Implementation
class PaymentProcessorImpl implements PaymentProcessor {

    private final PaymentGateway gateway;

    public PaymentProcessorImpl(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public double calculateFee(Ticket t, double hourlyRate) {
        if (t == null || t.getVehicle() == null) return 0.0;

        // making sure that exitTime is set so the duration is defined
        if (t.getExitTime() == null) {
            t.setExitTime(java.time.LocalDateTime.now());
        }

        java.time.Duration d = java.time.Duration.between(t.getEntryTime(), t.getExitTime());

        double amount;
        if (hourlyRate > 0) {
            // calculate based on slot’s custom rate
            double hours = d.toMinutes() / 60.0;
            amount = hourlyRate * hours;
        } else {
            // on Vehicle (Payable)
            if (t.getVehicle() instanceof Payable p) {
                amount = p.calculateFee(d);
            } else {
                amount = 0.0;
            }
        }

        t.setTotalFee(amount);
        return amount;
    }

    //simulate payment
    @Override
    public boolean takePayment(Ticket t, String cardToken) {
        if (t == null) return false;
        double amount = t.getTotalFee();
        String paymentId = gateway.authorize(amount, cardToken);
        return paymentId != null;
    }

    //generating the receipt
    @Override
    public String generateReceipt(Ticket t) {
        if (t == null) return "(no ticket)";
        return "Receipt for ticket #" + t.getTicketID() +
               "\nSlot: " + (t.getSlot() != null ? t.getSlot().getSlotID() : "-") +
               "\nVehicle: " + (t.getVehicle() != null ? t.getVehicle().getPlateNumber() : "-") +
               "\nEntry: " + t.getEntryTime() +
               "\nExit: " + t.getExitTime() +
               "\nTotal paid: $" + String.format("%.2f", t.getTotalFee());
    }
}
