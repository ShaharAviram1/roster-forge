package com.rosterforge.client;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ServerClient {

    private final String host;
    private final int port;
    private final Gson gson;

    public ServerClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.gson = new Gson();
    }

    public Response send(Request request) {
        try (
            Socket socket = new Socket(host, port);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            Scanner reader  = new Scanner(new InputStreamReader(socket.getInputStream()))
        ) {
            writer.println(gson.toJson(request));
            return gson.fromJson(reader.nextLine(), Response.class);
        } catch (IOException e) {
            return Response.error("Connection failed: " + e.getMessage());
        }
    }
}
