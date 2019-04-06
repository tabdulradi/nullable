# Nullable 
Wraps nullable values, offers interface similar scala.Option without the allocation cost

Notes:
- extends AnyVal
- Nullable(null).contains(null) == false
- zip/unzip works better
- No unless/when
- Avoid using hashCode or Pattern matching  https://github.com/scala/bug/issues/7396
- Nullable[Int](0).filter(_ > 5).orNull // won't compile