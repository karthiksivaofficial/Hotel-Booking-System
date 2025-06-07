package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PaymentService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    public String createPayment(BookingDTO bookingDTO, List<RoomDTO> rooms, String role) {
        try {
            APIContext apiContext = new APIContext(clientId, clientSecret, mode);

            Amount amount = new Amount();
            amount.setCurrency("USD");
            amount.setTotal(String.format("%.2f", bookingDTO.getTotalPrice()));

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription("Hotel booking for " + bookingDTO.getUserName());

            ItemList itemList = new ItemList();
            List<Item> items = new ArrayList<>();
            for (RoomDTO room : rooms) {
                Item item = new Item();
                item.setName("Room " + room.getRoomNumber());
                item.setCurrency("USD");
                item.setPrice(String.format("%.2f", room.getPrice()));
                item.setQuantity(String.valueOf(java.time.temporal.ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate())));
                items.add(item);
            }
            itemList.setItems(items);
            transaction.setItemList(itemList);

            List<Transaction> transactions = Collections.singletonList(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            String successUrl = role.equals("receptionist")
                    ? "http://localhost:8084/receptionist/offline-booking/success"
                    : "http://localhost:8084/user/bookings/success";
            String cancelUrl = role.equals("receptionist")
                    ? "http://localhost:8084/receptionist/offline-booking/cancel"
                    : "http://localhost:8084/user/bookings/cancel";

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setReturnUrl(successUrl);
            redirectUrls.setCancelUrl(cancelUrl);

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);
            payment.setRedirectUrls(redirectUrls);

            Payment createdPayment = payment.create(apiContext);
            for (Links link : createdPayment.getLinks()) {
                if ("approval_url".equals(link.getRel())) {
                    return link.getHref();
                }
            }
            throw new RuntimeException("Approval URL not found");
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Payment creation failed", e);
        }
    }

    public String executePayment(String paymentId, String payerId) {
        try {
            APIContext apiContext = new APIContext(clientId, clientSecret, mode);
            Payment payment = new Payment();
            payment.setId(paymentId);
            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);
            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            if ("approved".equals(executedPayment.getState())) {
                return executedPayment.getId();
            }
            throw new RuntimeException("Payment not approved");
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Payment execution failed", e);
        }
    }
}