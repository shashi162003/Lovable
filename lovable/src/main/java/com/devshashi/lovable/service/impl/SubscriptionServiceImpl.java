package com.devshashi.lovable.service.impl;

import com.devshashi.lovable.dto.subscription.CheckoutRequest;
import com.devshashi.lovable.dto.subscription.CheckoutResponse;
import com.devshashi.lovable.dto.subscription.PortalResponse;
import com.devshashi.lovable.dto.subscription.SubscriptionResponse;
import com.devshashi.lovable.service.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Override
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        return null;
    }

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId) {
        return null;
    }

    @Override
    public PortalResponse openCustomerPortal(Long userId) {
        return null;
    }
}
