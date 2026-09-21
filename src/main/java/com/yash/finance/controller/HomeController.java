package com.yash.finance.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Personal Finance Management API</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            max-width: 800px;
                            margin: 80px auto;
                            padding: 20px;
                            line-height: 1.6;
                            color: #222;
                        }
                        h1 {
                            margin-bottom: 10px;
                        }
                        .card {
                            padding: 25px;
                            border: 1px solid #ddd;
                            border-radius: 10px;
                        }
                        a {
                            display: inline-block;
                            margin-top: 15px;
                            padding: 10px 18px;
                            background: #1976d2;
                            color: white;
                            text-decoration: none;
                            border-radius: 6px;
                        }
                        a:hover {
                            background: #125ca1;
                        }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1>Personal Finance Management API</h1>

                        <p>
                            A Spring Boot REST API for managing personal finances,
                            including transactions, budgets, categories and financial analytics.
                        </p>

                        <p>
                            <strong>Backend Status:</strong> Running
                        </p>

                        <a href="/swagger-ui/index.html">
                            Open API Documentation (Swagger)
                        </a>
                    </div>
                </body>
                </html>
                """;
    }
}