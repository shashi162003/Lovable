package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.subscription.CheckoutRequest;
import com.devshashi.lovable.dto.subscription.CheckoutResponse;
import com.devshashi.lovable.dto.subscription.PortalResponse;
import com.devshashi.lovable.dto.subscription.SubscriptionResponse;
import com.devshashi.lovable.enums.SubscriptionStatus;

import java.time.Instant;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription();

    void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String gatewaySubscriptionId);

    void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String subId);

    boolean canCreateNewProject();
}
