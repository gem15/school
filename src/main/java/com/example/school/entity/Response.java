package com.example.school.entity;

import java.util.List;

public record Response(String error_code, List<Data> data) {
}
