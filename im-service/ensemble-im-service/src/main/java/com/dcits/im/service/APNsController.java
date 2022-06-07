package com.dcits.im.service;

import com.dcits.im.bc.redis.KeyValueRedisTemplate;
import com.dcits.im.constants.SessionConst;
import com.dcits.im.jpa.entity.ImRecCurrentUser;
import com.dcits.im.jpa.repository.ImRecCurrentUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/apns")
public class APNsController {
	@Resource
	private ImRecCurrentUserRepository currentUserRepository;
	@Resource
	private KeyValueRedisTemplate keyValueRedisTemplate;

	/**
	 * 开启apns
	 * @param userId 用户ID
	 * @param deviceToken 设备Token
	 * @return
	 */
	@PostMapping(value = "/open")
	public ResponseEntity<Void> open(@RequestParam String userId , @RequestParam String deviceToken) {
		keyValueRedisTemplate.openApns(userId, deviceToken);
		ImRecCurrentUser currentUser = new ImRecCurrentUser();
		currentUser.setUserId(userId);
		currentUser.setChannel(SessionConst.ChannelEnum.CHANNEL_IOS.getCode());
		currentUserRepository.update(currentUser);
		return ResponseEntity.ok().build();
	}


	/**
	 * 关闭apns
	 * @param uid
	 * @return
	 */
	@PostMapping(value = "/close")
	public ResponseEntity<Void> close(@RequestParam String uid) {
		keyValueRedisTemplate.closeApns(uid);
		ImRecCurrentUser currentUser = new ImRecCurrentUser();
		currentUser.setUserId(uid);
		currentUser.setChannel(SessionConst.ChannelEnum.CHANNEL_IOS.getCode());
		currentUserRepository.update(currentUser);
		return ResponseEntity.ok().build();
	}
}
