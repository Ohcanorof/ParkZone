package model;

//this and PaymentProcessor are to simulate payment, no real money is being moved around

public interface PaymentGateway {
    String authorize(double amount, String cardToken);
    boolean refund(String paymentID, double amount);
}

class PaymentGatewayImpl implements PaymentGateway {

    @Override
    public String authorize(double amount, String cardToken) {
        if (amount <= 0) {
            return null;
        }
        // here just faking an ID, again, just simulating
        return "PAY-" + System.currentTimeMillis();
    }

    @Override
    public boolean refund(String paymentID, double amount) {
        // simulation so it always succeeds
        return true;
    }
}