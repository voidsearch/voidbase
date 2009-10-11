#!/usr/local/bin/ruby

require 'net/http'

# check
raise "usage: ./post.rb [store] [key] [value]" if ARGV.size < 3

# create request
http = Net::HTTP.new('localhost', 8080)
path = "/cache/#{ARGV[0]}?key=#{ARGV[1]}"


headers = {
  'Content-Type' => 'text/plain'
}

resp, data = http.post(path, ARGV[2], headers)

puts 'Code = ' + resp.code
puts 'Message = ' + resp.message
resp.each {|key, val| puts key + ' = ' + val}
puts data
