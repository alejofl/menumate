export const selectStyles = (groupLeft, groupRight, lg = false) => ({
    container: (defaultStyle) => ({
        ...defaultStyle,
        flexGrow: 1
    }),
    control: (defaultStyle, state) => ({
        ...defaultStyle,
        width: "100%",
        height: "100%",
        flexGrow: 1,
        minWidth: "0",
        padding: `${lg ? "0.375rem" : "0"} 0.75rem`,
        marginLeft: "calc(var(--bs-border-width) * -1)",
        fontSize: "1rem",
        fontWeight: 400,
        lineHeight: 1.5,
        color: "var(--bs-body-color)",
        backgroundColor: "var(--bs-body-bg)",
        backgroundClip: "padding-box",
        WebkitAppearance: "none",
        MozAppearance: "none",
        appearance: "none",
        borderRadius: `${groupLeft ? "0" : "0.375rem" } ${groupRight ? "0" : "0.375rem" } ${groupRight ? "0" : "0.375rem" } ${groupLeft ? "0" : "0.375rem" }`,
        transition: "border-color .15s ease-in-out,box-shadow .15s ease-in-out",
        ":hover": {
            borderColor: state.isFocused ? "var(--primary)" : "var(--bs-border-color)"
        },
        boxShadow: state.isFocused ? "0 0 0 0.25rem #ed7a5740" : "none",
        border: state.isFocused ? "var(--bs-border-width) solid var(--primary)" : "var(--bs-border-width) solid var(--bs-border-color)"
    }),
    valueContainer: (defaultStyle) => ({
        ...defaultStyle,
        padding: 0
    }),
    placeholder: (defaultStyle) => ({
        ...defaultStyle,
        fontSize: "1rem",
        fontWeight: 400,
        lineHeight: 1.5,
        color: "var(--bs-secondary-color)"
    }),
    dropdownIndicator: (defaultStyle) => ({
        ...defaultStyle,
        color: "var(--bs-secondary-color)",
        paddingRight: 0
    }),
    option: (defaultStyle, state) => ({
        ...defaultStyle,
        backgroundColor: state.isSelected ? "var(--primary)" : state.isFocused ? "#ed7a5740" : "var(--bs-body-bg)",
        ":active": {
            backgroundColor: "var(--primary)",
            color: "white"
        }
    }),
    selectContainer: (defaultStyle) => ({
        ...defaultStyle,
        ":focus": {
            borderColor: "var(--primary)",
            outline: 0,
            boxShadow: "0 0 0 0.25rem #ed7a5740"
        }
    })
});

export const selectComponents = {
    IndicatorSeparator: () => null,
    ClearIndicator: () => null
};
