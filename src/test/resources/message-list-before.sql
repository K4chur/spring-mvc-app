delete from message;

insert into message(id, user_id, tag, text) VALUES
          (1, 1, 'my-tag', 'first'),
          (2, 1, 'more', 'second'),
          (3, 1, 'my-tag', 'third' ),
          (4, 1,  'another','fourth');

alter sequence message_seq restart with 10;