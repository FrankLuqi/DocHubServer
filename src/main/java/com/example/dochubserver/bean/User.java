package com.example.dochubserver.bean;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Date;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@DynamicUpdate
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private Integer status;
    private String userface;
    private String remark;
    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<OwnedRole> ownedRoles;

//    @Transient
//    private boolean isCredentialsNonExpired;
//    @Transient
//    private List<Role> roles;

    public User(){

    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (OwnedRole ownedRole:ownedRoles)
        {
            authorities.add(new SimpleGrantedAuthority(ownedRole.getDepartmentRoleId()));//返回该用户的部门角色id
        }
        return authorities;//将用户所拥有的角色返回到urlAccessDecisionManager中decide方法的第一个参数
    }

    /**
     * 账户是否过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否被锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 密码是否过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 用户是否被禁用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
