package base.template.application.ports.in.auth.command;

public record RegisterCommand(
    String email,
    String password
) {}
