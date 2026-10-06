package ru.phyllosedis.platform.banking.impl.service;

//@RequiredArgsConstructor
//@Service
public class UserServiceImpl /*implements UserService */ {

//    private final UserRepository userRepository;

//    @Override
//    public UUID registerUser(String name) {
//        if (name == null || name.isEmpty()) {
//            throw new UserNameCannotBeNullException(name);
//        }
//
//        User user = User.builder()
//                .id(UuidCreator.getTimeOrderedEpoch())
//                .name(name)
//                .build();
//        userRepository.save(user);
//        return user.getId();
//    }
//
//    @Override
//    public UserFindByIdResponseDto findById(UUID id) {
//        if (id == null) {
//            throw new UserIdCannotBeNullException();
//        }
//        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
//        return new UserFindByIdResponseDto(user.getId(), user.getName());
//    }
}
