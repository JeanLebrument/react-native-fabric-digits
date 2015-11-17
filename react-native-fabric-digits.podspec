Pod::Spec.new do |s|

    s.name                  = "react-native-fabric-digits"
    s.version               = "1.0.6"
    s.summary               = "react-native-fabric-digits"

    s.homepage              = "https://github.com/jeanlebrument/react-native-fabric-digits"

    s.author                = { "Jean Lebrument" => "jean@vimies.com" }

    s.platform              = :ios, '7.1'

    s.source                = { :git => "https://github.com/jeanlebrument/react-native-fabric-digits", :tag => s.version.to_s }

    s.source_files          = 'ios/RNFabricDigits/*.{h,m,swift}'

    s.dependency            'Digits'
    s.dependency            'Fabric'
    s.dependency            'TwitterCore'

end