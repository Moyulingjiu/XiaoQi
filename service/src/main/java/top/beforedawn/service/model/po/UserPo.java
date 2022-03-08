package top.beforedawn.service.model.po;

import java.time.LocalDateTime;

public class UserPo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.qq
     *
     * @mbg.generated
     */
    private Long qq;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.password
     *
     * @mbg.generated
     */
    private String password;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.last_change_password
     *
     * @mbg.generated
     */
    private LocalDateTime lastChangePassword;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.nickname
     *
     * @mbg.generated
     */
    private String nickname;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.use_nickname
     *
     * @mbg.generated
     */
    private Integer useNickname;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.right
     *
     * @mbg.generated
     */
    private Byte right;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.point
     *
     * @mbg.generated
     */
    private Integer point;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.luck
     *
     * @mbg.generated
     */
    private Integer luck;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.last_luck
     *
     * @mbg.generated
     */
    private LocalDateTime lastLuck;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.modified
     *
     * @mbg.generated
     */
    private LocalDateTime modified;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.modified_id
     *
     * @mbg.generated
     */
    private Long modifiedId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.create
     *
     * @mbg.generated
     */
    private LocalDateTime create;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.create_id
     *
     * @mbg.generated
     */
    private Long createId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.id
     *
     * @return the value of user.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.id
     *
     * @param id the value for user.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.qq
     *
     * @return the value of user.qq
     *
     * @mbg.generated
     */
    public Long getQq() {
        return qq;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.qq
     *
     * @param qq the value for user.qq
     *
     * @mbg.generated
     */
    public void setQq(Long qq) {
        this.qq = qq;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.password
     *
     * @return the value of user.password
     *
     * @mbg.generated
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.password
     *
     * @param password the value for user.password
     *
     * @mbg.generated
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.last_change_password
     *
     * @return the value of user.last_change_password
     *
     * @mbg.generated
     */
    public LocalDateTime getLastChangePassword() {
        return lastChangePassword;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.last_change_password
     *
     * @param lastChangePassword the value for user.last_change_password
     *
     * @mbg.generated
     */
    public void setLastChangePassword(LocalDateTime lastChangePassword) {
        this.lastChangePassword = lastChangePassword;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.nickname
     *
     * @return the value of user.nickname
     *
     * @mbg.generated
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.nickname
     *
     * @param nickname the value for user.nickname
     *
     * @mbg.generated
     */
    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.use_nickname
     *
     * @return the value of user.use_nickname
     *
     * @mbg.generated
     */
    public Integer getUseNickname() {
        return useNickname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.use_nickname
     *
     * @param useNickname the value for user.use_nickname
     *
     * @mbg.generated
     */
    public void setUseNickname(Integer useNickname) {
        this.useNickname = useNickname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.right
     *
     * @return the value of user.right
     *
     * @mbg.generated
     */
    public Byte getRight() {
        return right;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.right
     *
     * @param right the value for user.right
     *
     * @mbg.generated
     */
    public void setRight(Byte right) {
        this.right = right;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.point
     *
     * @return the value of user.point
     *
     * @mbg.generated
     */
    public Integer getPoint() {
        return point;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.point
     *
     * @param point the value for user.point
     *
     * @mbg.generated
     */
    public void setPoint(Integer point) {
        this.point = point;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.luck
     *
     * @return the value of user.luck
     *
     * @mbg.generated
     */
    public Integer getLuck() {
        return luck;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.luck
     *
     * @param luck the value for user.luck
     *
     * @mbg.generated
     */
    public void setLuck(Integer luck) {
        this.luck = luck;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.last_luck
     *
     * @return the value of user.last_luck
     *
     * @mbg.generated
     */
    public LocalDateTime getLastLuck() {
        return lastLuck;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.last_luck
     *
     * @param lastLuck the value for user.last_luck
     *
     * @mbg.generated
     */
    public void setLastLuck(LocalDateTime lastLuck) {
        this.lastLuck = lastLuck;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.modified
     *
     * @return the value of user.modified
     *
     * @mbg.generated
     */
    public LocalDateTime getModified() {
        return modified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.modified
     *
     * @param modified the value for user.modified
     *
     * @mbg.generated
     */
    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.modified_id
     *
     * @return the value of user.modified_id
     *
     * @mbg.generated
     */
    public Long getModifiedId() {
        return modifiedId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.modified_id
     *
     * @param modifiedId the value for user.modified_id
     *
     * @mbg.generated
     */
    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.create
     *
     * @return the value of user.create
     *
     * @mbg.generated
     */
    public LocalDateTime getCreate() {
        return create;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.create
     *
     * @param create the value for user.create
     *
     * @mbg.generated
     */
    public void setCreate(LocalDateTime create) {
        this.create = create;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.create_id
     *
     * @return the value of user.create_id
     *
     * @mbg.generated
     */
    public Long getCreateId() {
        return createId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.create_id
     *
     * @param createId the value for user.create_id
     *
     * @mbg.generated
     */
    public void setCreateId(Long createId) {
        this.createId = createId;
    }
}