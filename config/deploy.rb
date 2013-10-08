require 'capistrano/ext/multistage'
require 'capistrano/confirm'
require "rvm/capistrano"                               # Load RVM's capistrano plugin.

set :stages, %w(production staging)
set :default_stage, "staging"
set :current_frontend, "frontend-0.2"

set :user, 'ubuntu'
set :scm, :git
set :scm_verbose, false
set :keep_releases, 2

set :user, 'ubuntu'
set :use_sudo, false
set :confirm_stages, "production"

set :rvm_type, :system
set :rvm_bin_path, '/usr/local/rvm/bin'

# set :scm, :git # You can set :scm explicitly or Capistrano will make an intelligent guess based on known version control directory names
# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`

role :web, "julgamento.clipesebandas.com.br"                          # Your HTTP server, Apache/etc
role :app, "julgamento.clipesebandas.com.br"                          # This may be the same as your `Web` server
role :db,  "julgamento.clipesebandas.com.br", :primary => true # This is where Rails migrations will run

# if you want to clean up old releases on each deploy uncomment this:
after "deploy:update_code", "deploy:cleanup"
#after "deploy:cleanup", "play:run"

# namespace :play do
#   task :run do    
#     run "cd #{release_path} ; play run"
#   end
# end