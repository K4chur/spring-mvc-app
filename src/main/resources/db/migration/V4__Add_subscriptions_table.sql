create table user_subscriptions(
    channel_id int8 NOT NULL references usr,
    subscriber_id int8 NOT NULL references usr,
    primary key (channel_id, subscriber_id)
)