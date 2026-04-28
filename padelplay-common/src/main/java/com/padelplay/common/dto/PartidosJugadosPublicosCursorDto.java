package com.padelplay.common.dto;

import java.util.ArrayList;
import java.util.List;

public class PartidosJugadosPublicosCursorDto {

    private List<PartidoJugadoPublicoDto> items = new ArrayList<>();
    private String nextCursor;
    private String previousCursor;
    private boolean hasNext;
    private boolean hasPrevious;

    public PartidosJugadosPublicosCursorDto() {
    }

    public List<PartidoJugadoPublicoDto> getItems() {
        return items;
    }

    public void setItems(List<PartidoJugadoPublicoDto> items) {
        this.items = items;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    public String getPreviousCursor() {
        return previousCursor;
    }

    public void setPreviousCursor(String previousCursor) {
        this.previousCursor = previousCursor;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
