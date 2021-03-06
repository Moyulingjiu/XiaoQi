package top.beforedawn.service.model.po;

import java.time.LocalDateTime;

public class AbstractPo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column abstract.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column abstract.text
     *
     * @mbg.generated
     */
    private String text;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column abstract.type
     *
     * @mbg.generated
     */
    private Byte type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column abstract.modified
     *
     * @mbg.generated
     */
    private LocalDateTime modified;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column abstract.modified_id
     *
     * @mbg.generated
     */
    private Long modifiedId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column abstract.create
     *
     * @mbg.generated
     */
    private LocalDateTime create;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column abstract.create_id
     *
     * @mbg.generated
     */
    private Long createId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column abstract.id
     *
     * @return the value of abstract.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column abstract.id
     *
     * @param id the value for abstract.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column abstract.text
     *
     * @return the value of abstract.text
     *
     * @mbg.generated
     */
    public String getText() {
        return text;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column abstract.text
     *
     * @param text the value for abstract.text
     *
     * @mbg.generated
     */
    public void setText(String text) {
        this.text = text == null ? null : text.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column abstract.type
     *
     * @return the value of abstract.type
     *
     * @mbg.generated
     */
    public Byte getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column abstract.type
     *
     * @param type the value for abstract.type
     *
     * @mbg.generated
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column abstract.modified
     *
     * @return the value of abstract.modified
     *
     * @mbg.generated
     */
    public LocalDateTime getModified() {
        return modified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column abstract.modified
     *
     * @param modified the value for abstract.modified
     *
     * @mbg.generated
     */
    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column abstract.modified_id
     *
     * @return the value of abstract.modified_id
     *
     * @mbg.generated
     */
    public Long getModifiedId() {
        return modifiedId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column abstract.modified_id
     *
     * @param modifiedId the value for abstract.modified_id
     *
     * @mbg.generated
     */
    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column abstract.create
     *
     * @return the value of abstract.create
     *
     * @mbg.generated
     */
    public LocalDateTime getCreate() {
        return create;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column abstract.create
     *
     * @param create the value for abstract.create
     *
     * @mbg.generated
     */
    public void setCreate(LocalDateTime create) {
        this.create = create;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column abstract.create_id
     *
     * @return the value of abstract.create_id
     *
     * @mbg.generated
     */
    public Long getCreateId() {
        return createId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column abstract.create_id
     *
     * @param createId the value for abstract.create_id
     *
     * @mbg.generated
     */
    public void setCreateId(Long createId) {
        this.createId = createId;
    }
}