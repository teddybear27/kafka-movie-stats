package org.esgi.project.model;

public class ViewStats {
    public int id;
    public String title;
    public int start_only = 0;
    public int half = 0;
    public int full = 0;

    public ViewStats() {}

    public void add(String category) {
        switch (category) {
            case "start_only": start_only++; break;
            case "half": half++; break;
            case "full": full++; break;
        }
    }

    public int getTotal() {
        return start_only + half + full;
    }
}