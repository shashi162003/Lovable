package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.subscription.CheckoutRequest;
import com.devshashi.lovable.dto.subscription.CheckoutResponse;
import com.devshashi.lovable.dto.subscription.PortalResponse;
import com.devshashi.lovable.dto.subscription.SubscriptionResponse;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(Long userId);

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId);

    PortalResponse openCustomerPortal(Long userId);
}
