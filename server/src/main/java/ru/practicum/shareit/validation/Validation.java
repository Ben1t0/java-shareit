package ru.practicum.shareit.validation;

public interface Validation {
    /**
     * Marks for validation during creation.
     */
    interface OnCreate {}

    /**
     * Marks for validation during update.
     */
    interface OnUpdate {}

    /**
     * Marks for validation during partial update.
     */
    interface OnPatch {}
}