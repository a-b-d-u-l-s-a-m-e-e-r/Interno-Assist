package com.interno.assist.enums;

public enum PromptEnum {
    SUMMARIZE("summarize", """
                        Summarize the following content.

                        Requirements:
                        - Keep the important information.
                        - Remove unnecessary repetition.
                        - Use clear and simple language.
                        - Use a short heading if appropriate.
                        - Use bullet points when they improve readability.
                        - Do not add information that is not present in the original content.

                        Content:
                        
                        """),
    SUGGEST("suggest","""
                        Analyze the following content and suggest
                        related topics and further reading.

                        Requirements:
                        - Use clear headings.
                        - Use numbered or bullet lists.
                        - Suggest useful related concepts.
                        - Keep the suggestions relevant to the content.
                        - Do not invent unrelated topics.

                        Content:
                        
                        """),
    REWRITE("rewrite","""
                        Rewrite the following content.

                        Requirements:
                        - Improve clarity and readability.
                        - Keep the original meaning.
                        - Use professional and natural language.
                        - Do not add unnecessary information.
                        - Preserve important technical terms.

                        Content:
                        
                        """),
    GRAMMAR("grammar","""
                        Correct the grammar of the following content.

                        Requirements:
                        - Correct grammar, spelling and punctuation.
                        - Preserve the original meaning.
                        - Improve sentence structure where necessary.
                        - Do not unnecessarily rewrite the content.
                        - Return the corrected version directly.
                        - If the content is already grammatically correct, return it as is.

                        Content:
                        
                        """),
    EXPLAIN("explain","""
                        Explain the following content in simple and
                        easy-to-understand language.

                        Requirements:
                        - Explain difficult concepts clearly.
                        - Use examples where useful.
                        - Use headings and bullet points where appropriate.
                        - Assume the reader is learning the topic for the first time.
                        - Do not unnecessarily repeat the original text.

                        Content:
                        
                        """),
    IMPROVE("improve","""
                        Improve the following content.

                        Requirements:
                        - Improve clarity.
                        - Improve grammar.
                        - Improve readability.
                        - Improve sentence structure.
                        - Maintain the original meaning.
                        - Make the content professional and polished.

                        Content:
                        
                        """),
    SIMPLIFY("simplify","""
                        Simplify the following content.

                        Requirements:
                        - Use simple and easy-to-understand language.
                        - Preserve the original meaning.
                        - Remove unnecessary complexity.
                        - Keep important technical terms when necessary.
                        - Use short sentences where appropriate.

                        Content:
                        
                        """),
    TRANSLATE("translate","""
                        Translate the following content into
                        the requested language.

                        Requirements:
                        - Preserve the original meaning.
                        - Preserve technical terminology where appropriate.
                        - Keep the same structure where possible.
                        - Do not add explanations.
                        - Return only the translated content.

                        Target language:
                        
                        """)
    ;

    private final String code;
    private final String value;

    PromptEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
