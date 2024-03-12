package org.tre.engine;

import java.util.Set;

public class GameAction
{
    private final Set<String> subjects;
    private final Set<String> consumed;
    private final Set<String> produced;
    private final String narration;

    // Constructor for Action class
    public GameAction(Set<String> subjects, Set<String> consumed, Set<String> produced, String narration) {
        this.subjects = subjects;
        this.consumed = consumed;
        this.produced = produced;
        this.narration = narration;
    }

    public Set<String> getSubjects() { return subjects; }
    public Set<String> getConsumed() { return consumed; }
    public Set<String> getProduced() { return produced; }
    public String getNarration() { return narration; }

}
