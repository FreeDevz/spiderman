import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { Eye, EyeOff, Mail, Lock, User } from 'lucide-react';
import type { AppDispatch, RootState } from '../../store';
import { register as registerUser, clearError } from '../../store/slices/authSlice';
import { ROUTES, VALIDATION_RULES } from '../../constants';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';

// Validation schema
const registerSchema = z.object({
  name: z.string().min(1, 'Full name is required').max(100, 'Name must not exceed 100 characters'),
  email: z.string().email('Please enter a valid email address'),
  password: z.string()
    .min(VALIDATION_RULES.PASSWORD_MIN_LENGTH, `Password must be at least ${VALIDATION_RULES.PASSWORD_MIN_LENGTH} characters`)
    .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/, 'Password must contain mixed case, numbers, and special characters'),
  confirmPassword: z.string().min(1, 'Please confirm your password'),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ["confirmPassword"],
});

type RegisterFormData = z.infer<typeof registerSchema>;

const RegisterPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const { loading, error, isAuthenticated } = useSelector((state: RootState) => state.auth);
  const [showPassword, setShowPassword] = React.useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = React.useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const password = watch('password');

  useEffect(() => {
    if (isAuthenticated) {
      navigate(ROUTES.DASHBOARD);
    }
  }, [isAuthenticated, navigate]);

  useEffect(() => {
    dispatch(clearError());
  }, [dispatch]);

  const onSubmit = async (data: RegisterFormData) => {
    try {
      await dispatch(registerUser(data)).unwrap();
      navigate(ROUTES.DASHBOARD);
    } catch (error) {
      // Error is handled by the Redux slice
    }
  };

  const getPasswordStrength = (password: string) => {
    if (!password) return { strength: 0, color: 'gray', text: '' };
    
    let strength = 0;
    if (password.length >= VALIDATION_RULES.PASSWORD_MIN_LENGTH) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/\d/.test(password)) strength++;
    if (/[@$!%*?&]/.test(password)) strength++;

    const strengthMap = {
      1: { color: 'red', text: 'Very Weak' },
      2: { color: 'orange', text: 'Weak' },
      3: { color: 'yellow', text: 'Fair' },
      4: { color: 'lightgreen', text: 'Good' },
      5: { color: 'green', text: 'Strong' },
    };

    return { strength, ...strengthMap[strength as keyof typeof strengthMap] };
  };

  const passwordStrength = getPasswordStrength(password);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <div className="mx-auto h-12 w-12 flex items-center justify-center rounded-full bg-primary-100">
            <span className="text-2xl">✓</span>
          </div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            TodoApp
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Create your account
          </p>
        </div>

        <form className="mt-8 space-y-6" onSubmit={handleSubmit(onSubmit)}>
          {error && (
            <div className="bg-error-50 border border-error-200 rounded-lg p-4">
              <p className="text-sm text-error-600">{error}</p>
            </div>
          )}

          <div className="space-y-4">
            <Input
              label="Full Name"
              type="text"
              placeholder="John Doe"
              leftIcon={<User className="h-5 w-5" />}
              error={errors.name?.message}
              {...register('name')}
            />

            <Input
              label="Email address"
              type="email"
              placeholder="john@example.com"
              leftIcon={<Mail className="h-5 w-5" />}
              error={errors.email?.message}
              {...register('email')}
            />

            <div className="relative">
              <Input
                label="Password"
                type={showPassword ? 'text' : 'password'}
                placeholder="••••••••••••"
                leftIcon={<Lock className="h-5 w-5" />}
                rightIcon={
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="text-gray-400 hover:text-gray-600"
                  >
                    {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                  </button>
                }
                error={errors.password?.message}
                {...register('password')}
              />
              
              {/* Password strength indicator */}
              {password && (
                <div className="mt-2">
                  <div className="flex items-center space-x-2 text-xs">
                    <div className="flex-1 bg-gray-200 rounded-full h-2">
                      <div
                        className={`h-2 rounded-full transition-all duration-300 ${
                          passwordStrength.color === 'red' ? 'bg-red-500' :
                          passwordStrength.color === 'orange' ? 'bg-orange-500' :
                          passwordStrength.color === 'yellow' ? 'bg-yellow-500' :
                          passwordStrength.color === 'lightgreen' ? 'bg-green-400' :
                          'bg-green-500'
                        }`}
                        style={{ width: `${(passwordStrength.strength / 5) * 100}%` }}
                      />
                    </div>
                    <span className={`text-${passwordStrength.color === 'gray' ? 'gray' : passwordStrength.color}-600`}>
                      {passwordStrength.text}
                    </span>
                  </div>
                  
                  {/* Password requirements */}
                  <div className="mt-2 space-y-1 text-xs text-gray-600">
                    <div className={`flex items-center ${password.length >= VALIDATION_RULES.PASSWORD_MIN_LENGTH ? 'text-success-500' : 'text-gray-400'}`}>
                      <span className="mr-1">✓</span>
                      {VALIDATION_RULES.PASSWORD_MIN_LENGTH}+ characters
                    </div>
                    <div className={`flex items-center ${/[a-z]/.test(password) ? 'text-success-500' : 'text-gray-400'}`}>
                      <span className="mr-1">✓</span>
                      Lowercase letter
                    </div>
                    <div className={`flex items-center ${/[A-Z]/.test(password) ? 'text-success-500' : 'text-gray-400'}`}>
                      <span className="mr-1">✓</span>
                      Uppercase letter
                    </div>
                    <div className={`flex items-center ${/\d/.test(password) ? 'text-success-500' : 'text-gray-400'}`}>
                      <span className="mr-1">✓</span>
                      Number
                    </div>
                    <div className={`flex items-center ${/[@$!%*?&]/.test(password) ? 'text-success-500' : 'text-gray-400'}`}>
                      <span className="mr-1">✓</span>
                      Special character
                    </div>
                  </div>
                </div>
              )}
            </div>

            <div className="relative">
              <Input
                label="Confirm Password"
                type={showConfirmPassword ? 'text' : 'password'}
                placeholder="••••••••••••"
                leftIcon={<Lock className="h-5 w-5" />}
                rightIcon={
                  <button
                    type="button"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    className="text-gray-400 hover:text-gray-600"
                  >
                    {showConfirmPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                  </button>
                }
                error={errors.confirmPassword?.message}
                {...register('confirmPassword')}
              />
            </div>
          </div>

          <Button
            type="submit"
            variant="primary"
            size="lg"
            loading={loading}
            fullWidth
          >
            Create Account
          </Button>

          <div className="text-center">
            <p className="text-sm text-gray-600">
              Already have an account?{' '}
              <Link
                to={ROUTES.LOGIN}
                className="font-medium text-primary-600 hover:text-primary-500"
              >
                Sign in here
              </Link>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage; 