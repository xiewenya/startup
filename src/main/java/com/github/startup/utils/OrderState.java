package com.github.startup.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by bresai on 16/5/27.
 */
public enum OrderState {
    created(2), payed(1), failed(3);

    private int stateValue;

    OrderState(int stateValue) {
        this.stateValue = stateValue;
    }

    public int getStateValue() {
        return stateValue;
    }

    public void setStateValue(int stateValue) {
        this.stateValue = stateValue;
    }

    @JsonCreator
    public int to_represent(OrderState stateName) {
        for (OrderState state : OrderState.values()) {
            if (state == stateName) {
                return state.stateValue;
            }
        }
        return 0;
    }

    @JsonValue
    public OrderState to_internal_value(int stateValue) {
        for (OrderState state : OrderState.values()) {
            if (getStateValue() == stateValue) {
                return state;
            }
        }
        return OrderState.created;
    }
}
