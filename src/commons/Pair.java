package commons;

import lombok.Getter;

@Getter
public record Pair<T>(T first, T second) {}
