package com.example.orders.adapter.out.external;

import java.util.UUID;

/** Dados fixos compartilhados pelos fakes de catálogo e estoque. */
public final class SeedData {
    public static final UUID NOTEBOOK = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
    public static final UUID MOUSE    = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000002");
    public static final UUID KEYBOARD = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000003");

    private SeedData() {}
}
