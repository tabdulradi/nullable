# Nullable 
Wraps nullable values, offers interface similar scala.Option without the allocation cost

Notes:
- extends AnyVal
- `Nullable(null).contains(null) == false`
- zip/unzip works better
- No unless/when
- Avoid using hashCode or Pattern matching  https://github.com/scala/bug/issues/7396
- `Nullable[Int](3).filter(_ > 5).orNull` // won't even compile because Int is primitive (you won't get dummy values like 0)
