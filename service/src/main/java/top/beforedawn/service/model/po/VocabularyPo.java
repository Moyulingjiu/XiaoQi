package top.beforedawn.service.model.po;

import java.time.LocalDateTime;

public class VocabularyPo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vocabulary.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vocabulary.word
     *
     * @mbg.generated
     */
    private String word;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vocabulary.part
     *
     * @mbg.generated
     */
    private String part;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vocabulary.meaning
     *
     * @mbg.generated
     */
    private String meaning;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vocabulary.type
     *
     * @mbg.generated
     */
    private Byte type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vocabulary.modified
     *
     * @mbg.generated
     */
    private LocalDateTime modified;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vocabulary.modified_id
     *
     * @mbg.generated
     */
    private Long modifiedId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vocabulary.create
     *
     * @mbg.generated
     */
    private LocalDateTime create;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column vocabulary.create_id
     *
     * @mbg.generated
     */
    private Long createId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vocabulary.id
     *
     * @return the value of vocabulary.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vocabulary.id
     *
     * @param id the value for vocabulary.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vocabulary.word
     *
     * @return the value of vocabulary.word
     *
     * @mbg.generated
     */
    public String getWord() {
        return word;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vocabulary.word
     *
     * @param word the value for vocabulary.word
     *
     * @mbg.generated
     */
    public void setWord(String word) {
        this.word = word == null ? null : word.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vocabulary.part
     *
     * @return the value of vocabulary.part
     *
     * @mbg.generated
     */
    public String getPart() {
        return part;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vocabulary.part
     *
     * @param part the value for vocabulary.part
     *
     * @mbg.generated
     */
    public void setPart(String part) {
        this.part = part == null ? null : part.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vocabulary.meaning
     *
     * @return the value of vocabulary.meaning
     *
     * @mbg.generated
     */
    public String getMeaning() {
        return meaning;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vocabulary.meaning
     *
     * @param meaning the value for vocabulary.meaning
     *
     * @mbg.generated
     */
    public void setMeaning(String meaning) {
        this.meaning = meaning == null ? null : meaning.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vocabulary.type
     *
     * @return the value of vocabulary.type
     *
     * @mbg.generated
     */
    public Byte getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vocabulary.type
     *
     * @param type the value for vocabulary.type
     *
     * @mbg.generated
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vocabulary.modified
     *
     * @return the value of vocabulary.modified
     *
     * @mbg.generated
     */
    public LocalDateTime getModified() {
        return modified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vocabulary.modified
     *
     * @param modified the value for vocabulary.modified
     *
     * @mbg.generated
     */
    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vocabulary.modified_id
     *
     * @return the value of vocabulary.modified_id
     *
     * @mbg.generated
     */
    public Long getModifiedId() {
        return modifiedId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vocabulary.modified_id
     *
     * @param modifiedId the value for vocabulary.modified_id
     *
     * @mbg.generated
     */
    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vocabulary.create
     *
     * @return the value of vocabulary.create
     *
     * @mbg.generated
     */
    public LocalDateTime getCreate() {
        return create;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vocabulary.create
     *
     * @param create the value for vocabulary.create
     *
     * @mbg.generated
     */
    public void setCreate(LocalDateTime create) {
        this.create = create;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column vocabulary.create_id
     *
     * @return the value of vocabulary.create_id
     *
     * @mbg.generated
     */
    public Long getCreateId() {
        return createId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column vocabulary.create_id
     *
     * @param createId the value for vocabulary.create_id
     *
     * @mbg.generated
     */
    public void setCreateId(Long createId) {
        this.createId = createId;
    }
}