hibernate-redis
===============

hibernate 4 (4.2.3-Final) 2nd level cache using redis.

기존 Spring Data Redis 라이브러리를 제거하고, Jedis 만을 이용하여 개발했습니다.


### 주의 사항

Entity의 Cache region 을 지정하지 마세요. 기본적으로 RegionName은 Class Name입니다.
Redis의 특성상, 각 엔티티별로 Region을 따로 관리하도록 했습니다.