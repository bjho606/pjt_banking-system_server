package com.ssafy.ssapay.domain.user.entity;

import com.ssafy.ssapay.domain.oauth.OauthServerType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "oauth_member")
@Getter
public class OauthId implements Serializable {

    @Column(name = "oauth_server_id")
    private String oauthServerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_server")
    private OauthServerType oauthServerType;
}
